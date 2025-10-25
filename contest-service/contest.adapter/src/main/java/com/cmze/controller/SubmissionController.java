package com.cmze.controller;

import com.cmze.enums.SubmissionStatus;
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
    public ResponseEntity<?> submitForContest(@PathVariable("contestId") String contestId,
                                              @RequestPart(value = "name") String name,
                                              @RequestHeader("X-User-Id") String userId,
                                              @RequestPart("file") MultipartFile file
    ) {
        var result = submitEntryForContestUseCase.execute(contestId, userId, name, file);

        return result.toResponseEntity(HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSubmission(@PathVariable("contestId") String contestId,
                                              @PathVariable("id") String submissionId,
                                              @RequestHeader("X-User-Id") String requesterUserId) {
        var res = deleteSubmissionUseCase.execute(contestId, requesterUserId, submissionId);
        return res.toResponseEntity(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public ResponseEntity<?> list(@PathVariable String contestId,
                                  @RequestParam(name = "status", defaultValue = "PENDING") SubmissionStatus status,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "20") int size) {
        var res = listSubmissionsForReviewUseCase.execute(contestId, status, page, size);
        return res.toResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/{id}/media-url")
    public ResponseEntity<?> mediaUrl(@PathVariable String contestId,
                                      @PathVariable String submissionId,
                                      @RequestHeader("X-User-Id") String userId) {
        var res = getSubmissionMediaUrlUseCase.execute(contestId, submissionId, userId);
        return res.toResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/{id}/review")
    public ResponseEntity<?> review(@PathVariable("contestid") String contestId,
                                    @PathVariable("id") String submissionId,
                                    @RequestHeader("X-User-Id") String reviewerUserId,
                                    @RequestBody @Valid ReviewSubmissionRequest body) {
        var res = reviewSubmissionUseCase.execute(contestId, submissionId, reviewerUserId, body);
        return res.toResponseEntity(HttpStatus.NO_CONTENT);
    }
}
