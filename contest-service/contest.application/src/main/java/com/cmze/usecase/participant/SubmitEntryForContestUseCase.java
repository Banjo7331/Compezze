package com.cmze.usecase.participant;

import com.cmze.entity.Contest;
import com.cmze.entity.Submission;
import com.cmze.repository.ContestRepository;
import com.cmze.repository.SubmissionRepository;
import com.cmze.response.SubmitEntryResponse;
import com.cmze.shared.ActionResult;
import com.cmze.spi.minio.MinioService;
import com.cmze.usecase.UseCase;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@UseCase
public class SubmitEntryForContestUseCase {

    private final MinioService minioService;
    private final ContestRepository contestRepository;
    private final SubmissionRepository submissionRepository;

    @Value("${app.media.bucket:contest-media}")
    private String mediaBucket;

    @Value("${app.media.publicBaseUrl:}")
    private String publicBaseUrl;

    public SubmitEntryForContestUseCase(MinioService minioService,
                                        ContestRepository contestRepository,
                                        SubmissionRepository submissionRepository) {
        this.minioService = minioService;
        this.contestRepository = contestRepository;
        this.submissionRepository = submissionRepository;
    }

    @Transactional
    public ActionResult<SubmitEntryResponse> execute(String contestId,
                                                     String participantId,
                                                     MultipartFile file) {
        // --- 0) Walidacja wejścia ---
        if (file == null || file.isEmpty()) {
            return ActionResult.failure(ProblemDetail.forStatusAndDetail(
                    HttpStatus.BAD_REQUEST, "File is required and must be non-empty."));
        }
        String contentType = String.valueOf(file.getContentType());
        boolean allowed = contentType != null && (
                contentType.equals("image/jpeg") ||
                        contentType.equals("image/png")  ||
                        contentType.equals("image/webp") ||
                        contentType.equals("video/mp4")  ||
                        contentType.equals("video/quicktime")
        );
        if (!allowed) {
            return ActionResult.failure(ProblemDetail.forStatusAndDetail(
                    HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Only JPEG, PNG, WEBP, MP4 or MOV are allowed."));
        }
        long maxBytes = 50L * 1024 * 1024; // 50 MB przykładowo dla wideo
        if (file.getSize() > maxBytes) {
            return ActionResult.failure(ProblemDetail.forStatusAndDetail(
                    HttpStatus.PAYLOAD_TOO_LARGE, "File too large."));
        }

        // --- 1) Pobierz konkurs ---
        Optional<Contest> optContest = contestRepository.findById(contestId);
        if (optContest.isEmpty()) {
            return ActionResult.failure(ProblemDetail.forStatusAndDetail(
                    HttpStatus.NOT_FOUND, "Contest not found."));
        }
        Contest contest = optContest.get();

        // --- 2) Reguły biznesowe udziału ---
        if (!contest.isOpen()) {
            return ActionResult.failure(ProblemDetail.forStatusAndDetail(
                    HttpStatus.UNPROCESSABLE_ENTITY, "Contest is closed for submissions."));
        }
        LocalDateTime now = LocalDateTime.now();
        if (contest.getStartDate() != null && now.isBefore(contest.getStartDate())) {
            return ActionResult.failure(ProblemDetail.forStatusAndDetail(
                    HttpStatus.UNPROCESSABLE_ENTITY, "Submissions are not open yet."));
        }
        if (contest.getEndDate() != null && now.isAfter(contest.getEndDate())) {
            return ActionResult.failure(ProblemDetail.forStatusAndDetail(
                    HttpStatus.UNPROCESSABLE_ENTITY, "Submission period has ended."));
        }
        if (contest.getParticipantLimit() != null && contest.getParticipantLimit() > 0) {
            long current = submissionRepository.countByContest_Id(contestId);
            if (current >= contest.getParticipantLimit()) {
                return ActionResult.failure(ProblemDetail.forStatusAndDetail(
                        HttpStatus.CONFLICT, "Participant limit reached."));
            }
        }
        // 1 submission / participant (jeśli taki wymóg)
        if (submissionRepository.existsByContest_IdAndParticipantId(contestId, participantId)) {
            return ActionResult.failure(ProblemDetail.forStatusAndDetail(
                    HttpStatus.CONFLICT, "You have already submitted an entry for this contest."));
        }

        // --- 3) Upload do MinIO ---
        String objectKey = null;
        try {
            objectKey = generateObjectKey(contestId, participantId, file.getOriginalFilename());
            minioService.uploadFile(mediaBucket, file, objectKey, contentType);

            String publicUrl = (publicBaseUrl == null || publicBaseUrl.isBlank())
                    ? null
                    : buildPublicUrl(publicBaseUrl, objectKey);

            // --- 4) Zapis Submission w DB ---
            Submission sub = new Submission();
            sub.setId(null);
            sub.setContest(contest);
            sub.setParticipantId(participantId);
            sub.setOriginalFilename(file.getOriginalFilename());
            sub.setCreatedAt(LocalDateTime.now());
            sub.setFile(new Submission.FileRef(objectKey, publicUrl, contentType, file.getSize()));

            Submission saved = submissionRepository.save(sub);

            // --- 5) Response ---
            return ActionResult.success(new SubmitEntryResponse(saved.getId(), objectKey, publicUrl));

        } catch (Exception ex) {
            // kompensacja: usuń z MinIO, jeśli już wgrane
            if (objectKey != null) {
                try { minioService.delete(mediaBucket, objectKey); } catch (Exception ignore) {}
            }
            return ActionResult.failure(ProblemDetail.forStatusAndDetail(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Failed to submit entry."));
        }
    }

    /* ===== helpers ===== */

    private static String generateObjectKey(String contestId, String participantId, String originalFilename) {
        String safeContest = contestId.replaceAll("[^a-zA-Z0-9_-]", "_");
        String safeUser = participantId.replaceAll("[^a-zA-Z0-9_-]", "_");
        String ext = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf('.')); // np. ".jpg" / ".mp4"
        }
        return "contests/" + safeContest + "/submissions/" + safeUser + "/" + UUID.randomUUID() + ext;
    }

    private static String buildPublicUrl(String baseUrl, String key) {
        return baseUrl.endsWith("/") ? baseUrl + key : baseUrl + "/" + key;
    }
}
