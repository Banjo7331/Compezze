package com.cmze.usecase.contest;

import com.cmze.repository.ContestRepository;
import com.cmze.repository.StageRepository;
import com.cmze.request.ReorderStagesRequest;
import com.cmze.shared.ActionResult;
import com.cmze.usecase.UseCase;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

@UseCase
public class ReorderStagesUseCase {

    private final ContestRepository contestRepository;
    private final StageRepository stageRepository;

    public ReorderContestStagesUseCase(ContestRepository contestRepository,
                                       StageRepository stageRepository) {
        this.contestRepository = contestRepository;
        this.stageRepository = stageRepository;
    }

    @Transactional
    public ActionResult<Void> execute(String contestId,
                                      String organizerId,
                                      ReorderStagesRequest req) {

        var contestOpt = contestRepository.findById(contestId);
        if (contestOpt.isEmpty()) {
            return ActionResult.failure(ProblemDetail.forStatusAndDetail(
                    HttpStatus.NOT_FOUND, "Contest not found."
            ));
        }
        var contest = contestOpt.get();

        if (contest.getOrganizerId() != null && !contest.getOrganizerId().equals(organizerId)) {
            return ActionResult.failure(ProblemDetail.forStatusAndDetail(
                    HttpStatus.FORBIDDEN, "You are not the organizer of this contest."
            ));
        }

        var provided = req.stageIdsInOrder();
        if (provided == null || provided.isEmpty()) {
            return ActionResult.failure(ProblemDetail.forStatusAndDetail(
                    HttpStatus.BAD_REQUEST, "stageIdsInOrder is required."
            ));
        }

        var stages = stageRepository.findByContest_Id(contestId);
        if (stages.isEmpty()) {
            return ActionResult.failure(ProblemDetail.forStatusAndDetail(
                    HttpStatus.UNPROCESSABLE_ENTITY, "No stages to reorder."
            ));
        }

        var providedSet = new HashSet<>(provided);
        if (providedSet.size() != provided.size()) {
            return ActionResult.failure(ProblemDetail.forStatusAndDetail(
                    HttpStatus.UNPROCESSABLE_ENTITY, "Stage ids must be unique."
            ));
        }

        var idsInContest = stages.stream().map(Stage::getId).collect(Collectors.toSet());
        if (!idsInContest.equals(providedSet)) {
            return ActionResult.failure(ProblemDetail.forStatusAndDetail(
                    HttpStatus.UNPROCESSABLE_ENTITY, "Provided ids must match exactly contest stages."
            ));
        }

        Map<Long, Stage> byId = stages.stream().collect(Collectors.toMap(Stage::getId, s -> s));
        int pos = 1;
        for (Long id : provided) {
            byId.get(id).setPosition(pos++);
        }

        stageRepository.saveAll(stages);
        return ActionResult.success(null);
    }
}