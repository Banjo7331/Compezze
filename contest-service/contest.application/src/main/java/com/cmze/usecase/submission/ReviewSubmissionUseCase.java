package com.cmze.usecase.submission;

import com.cmze.entity.Submission;
import com.cmze.enums.ContestRole;
import com.cmze.enums.SubmissionStatus;
import com.cmze.repository.ParticipantRepository;
import com.cmze.repository.SubmissionRepository;
import com.cmze.request.ReviewSubmissionRequest;
import com.cmze.shared.ActionResult;
import com.cmze.usecase.UseCase;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@UseCase
public class ReviewSubmissionUseCase {

    private final SubmissionRepository submissionRepo;
    private final ParticipantRepository participantRepo;

    public ReviewSubmissionUseCase(SubmissionRepository submissionRepo,
                                   ParticipantRepository participantRepo) {
        this.submissionRepo = submissionRepo;
        this.participantRepo = participantRepo;
    }

    @Transactional
    public ActionResult<Void> execute(String contestId, String reviewerUserId, ReviewSubmissionRequest req) {
        // 1) autoryzacja: tylko Moderator w danym konkursie
        var pOpt = participantRepo.findByContest_IdAndUserId(contestId, reviewerUserId);
        if (pOpt.isEmpty()) {
            return ActionResult.failure(ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "Not a participant."));
        }
        Set<ContestRole> roles = Optional.ofNullable(pOpt.get().getRoles()).orElse(Set.of());
        if (!roles.contains(ContestRole.Moderator)) {
            return ActionResult.failure(ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "Not allowed. Moderator role required."));
        }

        // 2) zgłoszenie
        Submission s = submissionRepo.findByIdAndContest_Id(req.submissionId(), contestId).orElse(null);
        if (s == null) {
            return ActionResult.failure(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "Submission not found."));
        }

        // 3) reguły komentarza
        String comment = (req.comment() == null) ? null : req.comment().trim();
        if (req.status() == SubmissionStatus.REJECTED && (comment == null || comment.isEmpty())) {
            return ActionResult.failure(ProblemDetail.forStatusAndDetail(
                    HttpStatus.UNPROCESSABLE_ENTITY, "Comment is required when rejecting a submission."
            ));
        }
        if (req.status() == SubmissionStatus.APPROVED) {
            comment = null; // przy akceptacji czyścimy komentarz
        }

        // 4) zapis (prosto)
        boolean same = s.getStatus() == req.status() && Objects.equals(s.getComment(), comment);
        if (!same) {
            s.setStatus(req.status());
            s.setComment(comment);
            submissionRepo.save(s);
        }
        return ActionResult.success(null);
    }
}
