package com.cmze.controller;

import com.cmze.request.ReviewSubmissionRequest;
import com.cmze.usecase.submission.ReviewSubmissionUseCase;
import com.cmze.usecase.participant.SubmitEntryForContestUseCase;
import com.cmze.usecase.submission.DeleteSubmissionUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("contest/{contestId}/submission")
public class SubmissionController {

    private final SubmitEntryForContestUseCase submitEntryForContestUseCase;
    private final DeleteSubmissionUseCase deleteSubmissionUseCase;
    private final ReviewSubmissionUseCase reviewSubmissionUseCase;

    public SubmissionController(SubmitEntryForContestUseCase submitEntryForContestUseCase,
                                DeleteSubmissionUseCase deleteSubmissionUseCase,
                                ReviewSubmissionUseCase reviewSubmissionUseCase) {
        this.submitEntryForContestUseCase = submitEntryForContestUseCase;
        this.deleteSubmissionUseCase = deleteSubmissionUseCase;
        this.reviewSubmissionUseCase = reviewSubmissionUseCase;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> submitForContest(@PathVariable("id") String contestId,
                                              @RequestHeader("X-User-Id") String participantId,
                                              @RequestPart("file") MultipartFile file
    ) {
        var result = submitEntryForContestUseCase.execute(contestId, participantId, file);

        return result.toResponseEntity(HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}/submissions/{sid}")
    public ResponseEntity<?> deleteSubmission(@PathVariable("id") String contestId,
                                              @PathVariable("sid") String submissionId,
                                              @RequestHeader("X-User-Id") String requesterUserId) {
        var res = deleteSubmissionUseCase.execute(contestId, requesterUserId, submissionId);
        return res.toResponseEntity(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{id}/review")
    public ResponseEntity<?> review(@PathVariable("id") String contestId,
                                    @RequestHeader("X-User-Id") String reviewerUserId,
                                    @RequestBody @Valid ReviewSubmissionRequest body) {
        var res = reviewSubmissionUseCase.execute(contestId, reviewerUserId, body);
        return res.toResponseEntity(HttpStatus.NO_CONTENT);
    }
}
