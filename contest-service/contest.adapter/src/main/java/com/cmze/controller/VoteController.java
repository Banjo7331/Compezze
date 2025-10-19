package com.cmze.controller;

import com.cmze.request.VoteCommand;
import com.cmze.usecase.session.VoteSubmissionUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("contest/{contestId}")
public class VoteController {

    private final VoteSubmissionUseCase voteUseCase;

    public VoteController(VoteSubmissionUseCase voteUseCase) {
        this.voteUseCase = voteUseCase;
    }

    @PostMapping("/stages/{stageId}/vote")
    public ResponseEntity<?> voteForStage(@PathVariable String contestId,
                                          @PathVariable Long stageId,
                                          @AuthenticationPrincipal(expression = "name") String userId,
                                          @RequestBody VoteCommand cmd) {
        var result = voteUseCase.execute(contestId, stageId, userId, cmd);
        return result.toResponseEntity(HttpStatus.OK);
    }
}
