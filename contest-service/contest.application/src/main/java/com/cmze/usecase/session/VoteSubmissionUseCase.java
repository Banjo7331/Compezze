package com.cmze.usecase.session;

import com.cmze.request.VoteCommand;
import com.cmze.shared.ActionResult;
import com.cmze.spi.VotingContext;
import com.cmze.usecase.UseCase;
import jakarta.transaction.Transactional;

@UseCase
public class VoteSubmissionUseCase {

    private final VotingContext votingContext;

    public VoteSubmissionUseCase(VotingContext votingContext) {
        this.votingContext = votingContext;
    }

    @Transactional
    public ActionResult<Void> execute(String contestId, Long stageId, String userId, VoteCommand cmd) {
        if (stageId == null) {
            return ActionResult.failure(org.springframework.http.ProblemDetail.forStatusAndDetail(
                    org.springframework.http.HttpStatus.BAD_REQUEST, "stageId is required."));
        }

        return votingContext.submitForStage(contestId, stageId, userId, cmd);
    }
}
