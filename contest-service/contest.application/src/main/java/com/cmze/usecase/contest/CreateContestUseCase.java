package com.cmze.usecase.contest;

import com.cmze.entity.Contest;
import com.cmze.entity.Stage;
import com.cmze.repository.ContestRepository;
import com.cmze.request.CreateContestRequest;
import com.cmze.response.CreateContestResponse;
import com.cmze.shared.ActionResult;
import com.cmze.spi.minio.MediaLocation;
import com.cmze.spi.minio.MinioService;
import com.cmze.spi.minio.ObjectKeyFactory;
import com.cmze.usecase.UseCase;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@UseCase
public class CreateContestUseCase {

    private static final Logger logger = LoggerFactory.getLogger(CreateContestUseCase.class);

    private final ContestRepository contestRepository;
    private final ModelMapper modelMapper;
    private final MinioService minioService;
    private final ObjectKeyFactory objectKeyFactory;

    @Value("${app.media.publicBaseUrl:}")
    private String publicBaseUrl;

    public CreateContestUseCase(ContestRepository contestRepository,
                                ModelMapper modelMapper,
                                MinioService minioService,
                                ObjectKeyFactory objectKeyFactory) {
        this.contestRepository = contestRepository;
        this.modelMapper = modelMapper;
        this.minioService = minioService;
        this.objectKeyFactory = objectKeyFactory;
    }

    @Transactional
    public ActionResult<CreateContestResponse> execute(CreateContestRequest request, MultipartFile image, String organizerId) {

        if (image != null && !image.isEmpty() && request.getTemplateId() != null) {
            return ActionResult.failure(ProblemDetail.forStatusAndDetail(
                    HttpStatus.BAD_REQUEST, "Cannot provide both a new image and a templateId."));
        }

        if ((image == null || image.isEmpty()) && request.getTemplateId() == null) {
            return ActionResult.failure(ProblemDetail.forStatusAndDetail(
                    HttpStatus.BAD_REQUEST, "You must provide either a new image or a templateId."));
        }

        if (image == null || image.isEmpty()) {
            return ActionResult.failure(ProblemDetail.forStatusAndDetail(
                    HttpStatus.BAD_REQUEST, "Image is required and must be non-empty."));
        }
        String contentType = Objects.toString(image.getContentType(), "");
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
        // (Jeśli masz walidację 'isPrivate' vs 'socialMedia', dodaj ją tutaj)

        // --- 2. Logika Biznesowa ---
        MediaLocation location = null; // Używamy obiektu MediaLocation
        try {

            if (image != null && !image.isEmpty()) {
                // ----------
                // SCENARIUSZ A: Użytkownik wgrał WŁASNY plik
                // ----------

                // 2a. Walidacja bezpieczeństwa pliku (patrz Krok 4 poniżej)
                var validationResult = validateAndSanitizeImage(image);
                if (!validationResult.isSafe()) {
                    return ActionResult.failure(ProblemDetail.forStatusAndDetail(
                            HttpStatus.UNSUPPORTED_MEDIA_TYPE, validationResult.errorMessage()));
                }

                // 2b. Użyj Factory do wygenerowania nowej lokalizacji
                location = objectKeyFactory.generateForContestCover(organizerId, image.getOriginalFilename());
                contentType = image.getContentType();
                imageSize = image.getSize();

                // 2c. Wgraj plik
                minioService.upload(
                        location.getBucket(),
                        location.getObjectKey(),
                        image.getInputStream(), // Możesz użyć validationResult.getSanitizedInputStream()
                        imageSize,
                        contentType
                );

            } else {
                // ----------
                // SCENARIUSZ B: Użytkownik wybrał SZABLON
                // ----------

                // 2a. Pobierz lokalizację szablonu (zakładam, że templateId to klucz obiektu)
                MediaLocation templateLocation = new MediaLocation(publicBucket, request.getTemplateId());
                // LUB: MediaLocation templateLocation = objectKeyFactory.getTemplateLocation(request.getTemplateId());

                // 2b. WAŻNE: Musimy SKOPIOWAĆ plik szablonu do nowej, unikalnej lokalizacji
                // Nigdy nie używaj bezpośrednio referencji do szablonu!
                location = objectKeyFactory.generateForContestCover(organizerId, request.getTemplateId());

                // 2c. Skopiuj obiekt w MinIO
                var templateMetadata = minioService.copy(
                        templateLocation.getBucket(),
                        templateLocation.getObjectKey(),
                        location.getBucket(),
                        location.getObjectKey()
                );

                contentType = templateMetadata.getContentType();
                imageSize = templateMetadata.getSize();
            }

            location = objectKeyFactory.generateForContestCover(organizerId, image.getOriginalFilename());

            minioService.upload(
                    location.getBucket(),    // Przekazujemy Bucket
                    location.getObjectKey(), // Przekazujemy Klucz
                    image.getInputStream(),
                    image.getSize(),
                    contentType
            );

            String publicUrl = (publicBaseUrl == null || publicBaseUrl.isBlank())
                    ? null
                    : buildPublicUrl(publicBaseUrl, location.getObjectKey());

            // --- B. Mapowanie Konkursu (Contest) ---
            Contest contest = modelMapper.map(request, Contest.class);
            contest.setId(null); // Upewniamy się, że to nowa encja
            contest.setOrganizerId(organizerId);
            contest.setContentVerified(false);
            contest.setOpen(true);
            contest.setStatus(ContestStatus.DRAFT); // Ustawiamy domyślny status (krytyczne!)

            // Osadzamy referencję obrazu (Embedded)
            contest.setCoverImage(new Contest.CoverImageRef(
                    location.getObjectKey(),
                    publicUrl,
                    contentType,
                    image.getSize()
            ));

            // (Jeśli pole 'publishTargets' wróciło do DTO, zmapuj je tutaj)
            // contest.setPublishTargets(request.getPublishTargets() ...);

            // --- C. Mapowanie Etapów (Stages) - NOWA, POPRAWNA LOGIKA ---
            List<Stage> stages = new ArrayList<>();
            for (StageRequest stageDto : request.getStages()) {

                // Używamy metody pomocniczej do mapowania polimorficznego
                Stage stageEntity = mapStageRequestToEntity(stageDto);

                // Ustawiamy relację zwrotną (kluczowe dla JPA)
                stageEntity.setContest(contest);
                stages.add(stageEntity);
            }
            contest.setStages(stages); // Ustawiamy listę etapów w konkursie

            // --- D. Zapis w DB ---
            Contest saved = contestRepository.save(contest);

            // --- E. Response ---
            CreateContestResponse response = new CreateContestResponse(saved.getId());
            return ActionResult.success(response);

        } catch (Exception ex) {
            logger.error("Failed to create contest for organizer {}: {}", organizerId, ex.getMessage(), ex);

            // Kompensacja: Jeśli zapis do DB się nie udał, usuń wgrany plik z MinIO
            if (location != null) {
                try {
                    minioService.delete(location.getBucket(), location.getObjectKey());
                } catch (Exception e) {
                    logger.error("COMPENSATION FAILED: Could not delete MinIO object {}", location.getObjectKey(), e);
                }
            }
            return ActionResult.failure(ProblemDetail.forStatusAndDetail(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create contest."));
        }
    }

    /**
     * Metoda pomocnicza do mapowania polimorficznych DTOs na Encje.
     * Używa ModelMappera do automatycznego mapowania.
     */
    private Stage mapStageRequestToEntity(StageRequest dto) {
        // ModelMapper jest wystarczająco sprytny, aby zobaczyć, że pola
        // w JuryStageRequest pasują do pól w JuryVoteStage (jeśli nazwy są takie same).

        // Uwaga: To wymaga, aby ModelMapper był skonfigurowany jako Bean w Springu.

        return switch (dto.getType()) {
            case JURY_VOTE -> modelMapper.map(dto, JuryVoteStage.class);
            case QUIZ -> modelMapper.map(dto, QuizStage.class);
            case SURVEY -> modelMapper.map(dto, SurveyStage.class);
            case PUBLIC_VOTE -> modelMapper.map(dto, PublicStage.class);
            case GENERIC -> modelMapper.map(dto, GenericStage.class);
            // Nie potrzebujemy default, bo enum pokrywa wszystkie przypadki.
            // Walidacja DTO (@NotNull) gwarantuje, że 'type' nie jest null.
        };
    }

    // Ta metoda zostaje - jest specyficzna dla tego UseCase
    private static String buildPublicUrl(String baseUrl, String key) {
        // Usuwamy / z początku klucza, jeśli istnieje, aby uniknąć podwójnego //
        String cleanKey = key.startsWith("/") ? key.substring(1) : key;
        String cleanBaseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        return cleanBaseUrl + "/" + cleanKey;
    }
}
