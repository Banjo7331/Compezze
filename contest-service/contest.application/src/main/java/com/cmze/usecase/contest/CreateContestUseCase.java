package com.cmze.usecase.contest;

import com.cmze.entity.Contest;
import com.cmze.entity.Stage;
import com.cmze.enums.SocialPlatform;
import com.cmze.repository.ContestRepository;
import com.cmze.repository.StageJuryConfigRepository;
import com.cmze.repository.StagePublicConfigRepository;
import com.cmze.request.CreateContestRequest;
import com.cmze.response.CreateContestResponse;
import com.cmze.shared.ActionResult;
import com.cmze.spi.minio.MinioService;
import com.cmze.usecase.UseCase;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@UseCase
public class CreateContestUseCase {

    private final ContestRepository contestRepository;
    private final StagePublicConfigRepository stagePublicCfgRepo;
    private final StageJuryConfigRepository stageJuryCfgRepo;
    private final ModelMapper modelMapper;
    private final MinioService minioService;

    @Value("${app.media.bucket:contest-media}")
    private String mediaBucket;

    @Value("${app.media.publicBaseUrl:}")
    private String publicBaseUrl;

    public CreateContestUseCase(ContestRepository contestRepository,
                                ModelMapper modelMapper,
                                MinioService minioService) {
        this.contestRepository = contestRepository;
        this.modelMapper = modelMapper;
        this.minioService = minioService;
    }

    @Transactional
    public ActionResult<CreateContestResponse> execute(CreateContestRequest request, MultipartFile image, String organizerId) {

        if (image == null || image.isEmpty()) {
            return ActionResult.failure(ProblemDetail.forStatusAndDetail(
                    HttpStatus.BAD_REQUEST, "Image is required and must be non-empty."));
        }
        String contentType = java.util.Objects.toString(image.getContentType(), "");
        if (!(contentType.equals("image/jpeg") || contentType.equals("image/png") || contentType.equals("image/webp"))) {
            return ActionResult.failure(ProblemDetail.forStatusAndDetail(
                    HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Only JPEG, PNG or WEBP are allowed."));
        }
        long maxBytes = 5L * 1024 * 1024; // 5 MB
        if (image.getSize() > maxBytes) {
            return ActionResult.failure(ProblemDetail.forStatusAndDetail(
                    HttpStatus.PAYLOAD_TOO_LARGE, "Image must be <= 5 MB."));
        }
        if (request.getStartDate().isAfter(request.getEndDate())) {
            return ActionResult.failure(ProblemDetail.forStatusAndDetail(
                    HttpStatus.UNPROCESSABLE_ENTITY, "End date must be after start date."
            ));
        }
        if (request.isPrivate() && request.isPublishToSocialMedia()) {
            return ActionResult.failure(ProblemDetail.forStatusAndDetail(
                    HttpStatus.UNPROCESSABLE_ENTITY, "Private contests cannot be published to social media."
            ));
        }


        String objectKey = null; // do kompensacji
        try {
            objectKey = generateObjectKey(organizerId, image.getOriginalFilename());
            var ref = minioService.upload(
                    objectKey,
                    image.getInputStream(),
                    image.getSize(),
                    contentType
            );

            // publiczny URL tylko gdy masz skonfigurowany baseUrl (dla prywatnych bucketów będzie null)
            String publicUrl = (publicBaseUrl == null || publicBaseUrl.isBlank())
                    ? null
                    : buildPublicUrl(publicBaseUrl, objectKey);

            // --- 2) Mapowanie Contest + pola zarządzane wewnętrznie ---
            Contest contest = modelMapper.map(request, Contest.class);
            contest.setId(null);
            contest.setOrganizerId(organizerId);
            contest.setContentVerified(false);
            contest.setOpen(true);

            // publishTargets
            Set<SocialPlatform> targets = request.getPublishTo() == null
                    ? Set.of()
                    : EnumSet.copyOf(request.getPublishTo());
            contest.setPublishTargets(targets);

            // osadzamy referencję obrazu (Embedded)
            contest.setCoverImage(new Contest.CoverImageRef(
                    objectKey, publicUrl, contentType, image.getSize()
            ));

            List<Stage> stages = new ArrayList<>(request.getStages().size());
            for (int i = 0; i < request.getStages().size(); i++) {
                var sr = request.getStages().get(i);

                Stage st = new Stage();
                st.setId(null);
                st.setName(sr.getName());
                st.setType(sr.getType());
                st.setDurationMinutes(sr.getDurationMinutes());
                st.setPosition(sr.getPosition());
                st.setContest(contest);

                stages.add(st);
            }
            contest.setStages(stages);

            // --- 4) Zapis w DB ---
            Contest saved = contestRepository.save(contest);

            // --- 6) Response ---
            CreateContestResponse response = new CreateContestResponse(saved.getId());
            return ActionResult.success(response);

        } catch (Exception ex) {
            // kompensacja uploadu, jeśli DB/inna część padła po wgraniu pliku
            if (objectKey != null) {
                try { minioService.delete(objectKey); } catch (Exception ignore) {}
            }
            return ActionResult.failure(ProblemDetail.forStatusAndDetail(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create contest."));
        }
    }


    private static String generateObjectKey(String organizerId, String originalFilename) {
        String safeOrg = organizerId == null ? "unknown" : organizerId.replaceAll("[^a-zA-Z0-9_-]", "_");
        String ext = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf('.')); // np. ".jpg"
        }
        return "contests/" + safeOrg + "/" + UUID.randomUUID() + ext;
    }

    private static String buildPublicUrl(String baseUrl, String key) {
        return baseUrl.endsWith("/") ? baseUrl + key : baseUrl + "/" + key;
    }
}
