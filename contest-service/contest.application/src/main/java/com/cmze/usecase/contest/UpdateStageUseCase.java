package com.cmze.usecase.contest;

import com.cmze.entity.Stage;
import com.cmze.repository.StageRepository;
import com.cmze.request.UpdateStageRequest;
import com.cmze.response.UpdateStageResponse;
import com.cmze.shared.ActionResult;
import com.cmze.usecase.UseCase;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

@UseCase
public class UpdateStageUseCase {

    private final StageRepository stageRepository;

    public UpdateStageUseCase(StageRepository stageRepository) {
        this.stageRepository = stageRepository;
    }

    @Transactional
    public ActionResult<UpdateStageResponse> execute(String contestId,
                                                     Long stageId,
                                                     String organizerId,
                                                     UpdateStageRequest req) {
        var opt = stageRepository.findById(stageId);
        if (opt.isEmpty()) {
            return ActionResult.failure(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "Stage not found."));
        }
        Stage st = opt.get();

        if (st.getContest() == null || !contestId.equals(st.getContest().getId())) {
            return ActionResult.failure(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "Stage not found in this contest."));
        }

        var contest = st.getContest();
        if (contest.getOrganizerId() != null && !contest.getOrganizerId().equals(organizerId)) {
            return ActionResult.failure(ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "Not the organizer."));
        }

        // Partial update (bez position)
        if (req.getName() != null) st.setName(req.getName());
        if (req.getType() != null) st.setType(req.getType());
        if (req.getDurationMinutes() != null) st.setDurationMinutes(req.getDurationMinutes());

        stageRepository.save(st);
        return ActionResult.success(new UpdateStageResponse(st.getId()));
    }
}
