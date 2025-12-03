package com.cmze.usecase.submission;

import com.cmze.entity.Participant;
import com.cmze.entity.Submission;
import com.cmze.repository.ParticipantRepository;
import com.cmze.repository.SubmissionRepository;
import com.cmze.response.GetSubmissionMediaUrlResponse;
import com.cmze.shared.ActionResult;
import com.cmze.spi.minio.MinioService;
import com.cmze.usecase.UseCase;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.net.URL;
import java.time.Duration;

@UseCase
public class GetSubmissionMediaUrlUseCase {

    private final SubmissionRepository submissionRepo;
    private final ParticipantRepository participantRepo;
    private final MinioService minio;


    @Value("${app.media.private.bucket}")
    private String privateBucket;

    @Value("${app.media.presigned-get-ttl:10m}")
    private Duration presignedGetTtl;

    public GetSubmissionMediaUrlUseCase(SubmissionRepository submissionRepo,
                                        ParticipantRepository participantRepo,
                                        MinioService minio) {
        this.submissionRepo = submissionRepo;
        this.participantRepo = participantRepo;
        this.minio = minio;
    }

//    @Transactional
//    public ActionResult<GetSubmissionMediaUrlResponse> execute(String contestId, String submissionId, String userId) {
//
//        // 1) autoryzacja: user musi być uczestnikiem TEGO konkursu
//        Participant participant = participantRepo.findsByContestIdAndUserId(contestId, userId);
//        if (participant == null) {
//            return ActionResult.failure(ProblemDetail.forStatusAndDetail(
//                    HttpStatus.FORBIDDEN,"Not a participant of this contest."));
//        }
//
//        // 2) wczytaj submission i upewnij się, że należy do tego konkursu
//        Submission submission = submissionRepo.findById(submissionId);
//        if (submission == null){
//            return ActionResult.failure(ProblemDetail.forStatusAndDetail(
//                    HttpStatus.NOT_FOUND,"Submission not found."));
//        }
//        String objectKey = submission.getFile().getObjectKey();
//        URL presignedUrl = minio.presignGet(privateBucket, objectKey, presignedGetTtl);
//        int expiresInSec = (int) presignedGetTtl.getSeconds();
//
//        GetSubmissionMediaUrlResponse response = new GetSubmissionMediaUrlResponse(
//                presignedUrl.toString(),
//                expiresInSec,
//                submission.getFile().getContentType(),
//                submission.getFile().getSize(),
//                submission.getOriginalFilename()
//        );
//        return ActionResult.success(response);
//    }
}
