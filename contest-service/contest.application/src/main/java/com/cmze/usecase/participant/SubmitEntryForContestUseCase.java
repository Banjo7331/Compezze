package com.cmze.usecase.participant;

import com.cmze.entity.Contest;
import com.cmze.entity.Participant;
import com.cmze.entity.Submission;
import com.cmze.enums.ContestRole;
import com.cmze.enums.SubmissionMediaPolicy;
import com.cmze.repository.ContestRepository;
import com.cmze.repository.ParticipantRepository;
import com.cmze.repository.SubmissionRepository;
import com.cmze.response.SubmitEntryResponse;
import com.cmze.shared.ActionResult;
import com.cmze.shared.MediaRef;
import com.cmze.spi.minio.MediaLocation;
import com.cmze.spi.minio.MinioService;
import com.cmze.spi.minio.ObjectKeyFactory;
import com.cmze.usecase.UseCase;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@UseCase
public class SubmitEntryForContestUseCase {

//    private final MinioService minioService;
//    private final ContestRepository contestRepository;
//    private final SubmissionRepository submissionRepository;
//    private final ParticipantRepository participantRepository;
//    private final ObjectKeyFactory objectKeyFactory;
//
//    @Value("${app.media.max-image-size:10MB}")
//    private DataSize maxImageSize;
//
//    @Value("${app.media.max-video-size:50MB}")
//    private DataSize maxVideoSize;
//
//    public SubmitEntryForContestUseCase(MinioService minioService,
//                                        ContestRepository contestRepository,
//                                        SubmissionRepository submissionRepository,
//                                        ParticipantRepository participantRepository,
//                                        ObjectKeyFactory objectKeyFactory) {
//        this.minioService = minioService;
//        this.contestRepository = contestRepository;
//        this.submissionRepository = submissionRepository;
//        this.participantRepository = participantRepository;
//        this.objectKeyFactory = objectKeyFactory;
//    }
//
//    @Transactional
//    public ActionResult<SubmitEntryResponse> execute(String contestId,
//                                                     String userId,
//                                                     String name,
//                                                     MultipartFile file) {
//        // --- 0) Walidacja wejścia ---
//        if (name == null || name.isBlank()) {                                    // ★
//            return ActionResult.failure(ProblemDetail.forStatusAndDetail(
//                    HttpStatus.BAD_REQUEST, "Name is required and must be non-empty."));
//        }
//        if (file == null || file.isEmpty()) {
//            return ActionResult.failure(ProblemDetail.forStatusAndDetail(
//                    HttpStatus.BAD_REQUEST, "File is required and must be non-empty."));
//        }
//
//        // --- 1) Pobierz konkurs ---
//        Contest contest = contestRepository.findById(contestId);
//        if (contest == null) {
//            return ActionResult.failure(ProblemDetail.forStatusAndDetail(
//                    HttpStatus.NOT_FOUND, "Contest not found."));
//        }
//
//        SubmissionMediaPolicy policy = contest.getSubmissionMediaPolicy();
//
//        ProblemDetail pd = validateFileAgainstPolicy(
//                policy, file, maxImageSize, maxVideoSize
//        );
//        if (pd != null) return ActionResult.failure(pd);
//
//        // --- 2) Reguły biznesowe udziału ---
//        if (!contest.isOpen()) {
//            return ActionResult.failure(ProblemDetail.forStatusAndDetail(
//                    HttpStatus.UNPROCESSABLE_ENTITY, "Contest is closed for submissions."));
//        }
//        LocalDateTime now = LocalDateTime.now();
//        if (contest.getStartDate() != null && now.isBefore(contest.getStartDate())) {
//            return ActionResult.failure(ProblemDetail.forStatusAndDetail(
//                    HttpStatus.UNPROCESSABLE_ENTITY, "Submissions are not open yet."));
//        }
//        if (contest.getEndDate() != null && now.isAfter(contest.getEndDate())) {
//            return ActionResult.failure(ProblemDetail.forStatusAndDetail(
//                    HttpStatus.UNPROCESSABLE_ENTITY, "Submission period has ended."));
//        }
//        if (contest.getParticipantLimit() != null && contest.getParticipantLimit() > 0) {
//            long current = submissionRepository.countByContest_Id(contestId);
//            if (current >= contest.getParticipantLimit()) {
//                return ActionResult.failure(ProblemDetail.forStatusAndDetail(
//                        HttpStatus.CONFLICT, "Participant limit reached."));
//            }
//        }
//
//        Participant participant = participantRepository.findsByContestIdAndUserId(contestId, userId);
//        if (participant == null) {
//            if (contest.getParticipantLimit()!=null && contest.getParticipantLimit() > 0) {
//                long currentParticipants = participantRepository.countByContestId(contestId);
//                if (currentParticipants >= contest.getParticipantLimit()) {
//                    return ActionResult.failure(ProblemDetail.forStatusAndDetail(
//                            HttpStatus.CONFLICT, "Participant limit reached."));
//                }
//            }
//            participant = new Participant();
//            participant.setContest(contest);
//            participant.setUserId(userId);
//            participant.getRoles().add(ContestRole.Competitor);
//            participant.setUpdatedAt(now);
//            participantRepository.save(participant);
//        }
//        // 1 submission / participant (jeśli taki wymóg)
//        if (submissionRepository.existsByContest_IdAndParticipantId(contestId, participant.getId())) {
//            return ActionResult.failure(ProblemDetail.forStatusAndDetail(
//                    HttpStatus.CONFLICT, "You have already submitted an entry for this contest."));
//        }
//
//        // --- 3) Upload do MinIO ---
//        MediaLocation mediaLocation = objectKeyFactory.generateForSubmission(contestId, userId, file.getOriginalFilename());
//        String bucket = mediaLocation.getBucket();
//        String objectKey = mediaLocation.getKey();
//
//        try (InputStream in = file.getInputStream()) {
//            MediaRef media = minioService.upload(
//                    bucket,
//                    objectKey,
//                    in,
//                    file.getSize(),
//                    file.getContentType()
//            );
//
//            // --- 6) Zapis Submission w DB ---
//            Submission submission = new Submission();
//            submission.setContest(contest);
//            submission.setParticipant(participant);
//            submission.setOriginalFilename(file.getOriginalFilename());
//            submission.setCreatedAt(LocalDateTime.now());
//
//            String savedKey = media.getObjectKey();
//            String savedContentType = media.getContentType() != null ? media.getContentType() : file.getContentType();
//            long savedSize = media.getBytes() > 0 ? media.getBytes() : file.getSize();
//
//            submission.setFile(new Submission.FileRef(
//                    savedKey,
//                    null,
//                    savedContentType,
//                    savedSize
//            ));
//
//            Submission saved = submissionRepository.save(submission);
//
//            // --- 7) Response (publicUrl = null; do odtwarzania użyjesz presigned GET) ---
//            return ActionResult.success(new SubmitEntryResponse(
//                    saved.getId(),
//                    savedKey,
//                    null
//            ));
//
//        } catch (Exception ex) {
//            // kompensacja: spróbuj usunąć obiekt z tego samego bucketu/klucza
//            try { minioService.delete(bucket, objectKey); } catch (Exception ignore) {}
//            return ActionResult.failure(ProblemDetail.forStatusAndDetail(
//                    HttpStatus.INTERNAL_SERVER_ERROR, "Failed to submit entry."));
//        }
//    }
//
//    /* ===== helpers ===== */
//
//    private static ProblemDetail validateFileAgainstPolicy(
//            SubmissionMediaPolicy policy,
//            MultipartFile file,
//            DataSize maxImageSize,
//            DataSize maxVideoSize
//    ) {
//        String contentType = file.getContentType() != null ? file.getContentType().toLowerCase() : "";
//        String ext = extOf(file.getOriginalFilename());
//
//        boolean isImage = contentType.startsWith("image/") || extIn(ext, ".jpg", ".jpeg", ".png", ".webp");
//        boolean isVideo = contentType.startsWith("video/") || extIn(ext, ".mp4", ".mov", ".m4v");
//
//        // --- typy wg polityki ---
//        if (policy == SubmissionMediaPolicy.IMAGES_ONLY && !isImage) {
//            return ProblemDetail.forStatusAndDetail(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
//                    "This contest accepts images only (JPG, PNG, WEBP).");
//        }
//        if (policy == SubmissionMediaPolicy.VIDEOS_ONLY && !isVideo) {
//            return ProblemDetail.forStatusAndDetail(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
//                    "This contest accepts videos only (MP4, MOV).");
//        }
//        if (policy == SubmissionMediaPolicy.BOTH && !(isImage || isVideo)) {
//            return ProblemDetail.forStatusAndDetail(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
//                    "Only images (JPG, PNG, WEBP) or videos (MP4, MOV) are allowed.");
//        }
//
//        // --- rozszerzenia (dodatkowa ochrona) ---
//        if (isImage && !extIn(ext, ".jpg", ".jpeg", ".png", ".webp")) {
//            return ProblemDetail.forStatusAndDetail(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
//                    "Allowed images: JPG, PNG, WEBP.");
//        }
//        if (isVideo && !extIn(ext, ".mp4", ".mov", ".m4v")) {
//            return ProblemDetail.forStatusAndDetail(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
//                    "Allowed videos: MP4, MOV.");
//        }
//
//        // --- rozmiary wg configu ---
//        long size = file.getSize();
//        if (isImage && size > maxImageSize.toBytes()) {
//            return ProblemDetail.forStatusAndDetail(
//                    HttpStatus.PAYLOAD_TOO_LARGE,
//                    "Image too large. Max " + maxImageSize.toMegabytes() + " MB.");
//        }
//        if (isVideo && size > maxVideoSize.toBytes()) {
//            return ProblemDetail.forStatusAndDetail(
//                    HttpStatus.PAYLOAD_TOO_LARGE,
//                    "Video too large. Max " + maxVideoSize.toMegabytes() + " MB.");
//        }
//
//        return null;
//    }
//
//
//    private static String extOf(String filename) {
//        if (filename == null) return "";
//        int i = filename.lastIndexOf('.');
//        return (i >= 0) ? filename.substring(i).toLowerCase() : "";
//    }
//    private static boolean extIn(String ext, String... allowed) {
//        for (String a : allowed) if (ext.equals(a)) return true;
//        return false;
//    }

}
