package com.cmze.usecase.submission;

import com.cmze.enums.SubmissionStatus;
import com.cmze.repository.SubmissionRepository;
import com.cmze.shared.ActionResult;
import com.cmze.usecase.UseCase;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@UseCase
public class ListSubmissionsForReviewUseCase {

    private final SubmissionRepository submissionRepo;

    public ListSubmissionsForReviewUseCase(SubmissionRepository submissionRepo) {
        this.submissionRepo = submissionRepo;
    }

    @Transactional
    public ActionResult<Page<SubmissionDto>> execute(String contestId, SubmissionStatus status, int page, int size) {
        var p = PageRequest.of(Math.max(0,page), Math.max(1,size), Sort.by("id").descending());
        var result = submissionRepo.findByContest_IdAndStatus(contestId, status, p)
                .map(SubmissionDto::from);
        return ActionResult.success(result);
    }

    @Getter
    @AllArgsConstructor
    public static class SubmissionDto {
        private String id;
        private SubmissionStatus status;
        private String rejectionReason;

        public static SubmissionDto from(Submission s) {
            return new SubmissionDto(s.getId(), s.getStatus(), s.getRejectionReason());
        }
    }
}