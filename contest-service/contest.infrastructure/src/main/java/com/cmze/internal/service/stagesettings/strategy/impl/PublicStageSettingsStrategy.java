package com.cmze.internal.service.stagesettings.strategy.impl;

import com.cmze.entity.Stage;
import com.cmze.entity.stagesettings.PublicVoteStage;
import com.cmze.enums.StageType;
import com.cmze.internal.service.stagesettings.strategy.StageSettingsStrategy;
import com.cmze.repository.VoteMarkerRepository;
import com.cmze.request.StageRequest;
import com.cmze.request.UpdateStageRequest;
import com.cmze.response.stagesettings.StageSettingsResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class PublicStageSettingsStrategy implements StageSettingsStrategy {

    private final VoteMarkerRepository voteMarkerRepository;

    public PublicStageSettingsStrategy(final VoteMarkerRepository voteMarkerRepository) {
        this.voteMarkerRepository  = voteMarkerRepository;
    }

    @Override
    public StageType type() {
        return StageType.PUBLIC_VOTE;
    }

    @Override
    public ProblemDetail validate(final StageRequest dto) {
        if (!(dto instanceof StageRequest.PublicStageRequest req)) {
            return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Invalid DTO for PUBLIC strategy");
        }
        if (req.getWeight() != null && req.getWeight() <= 0.0) {
            return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, "Weight must be > 0");
        }
        return null;
    }

    @Override
    public Stage createStage(final StageRequest dto) {
        if (dto instanceof StageRequest.PublicStageRequest req) {
            return PublicVoteStage.builder()
                    .weight(req.getWeight())
                    .maxScore(req.getMaxScore())
                    .build();
        }
        throw new IllegalArgumentException("Invalid DTO for PUBLIC strategy");
    }

    @Override
    public void updateStage(UpdateStageRequest dto, Stage stage) {

    }

    @Override
    public StageSettingsResponse runStage(final long stageId) {
//        return new StageSettingsResponse(stageId, "PUBLIC_VOTE", null);
        return null;
    }

    @Override
    public Map<UUID, Double> collectResults(final Stage stage) {
//        if (!(stage instanceof PublicVoteStage publicStage)) {
//            throw new IllegalStateException("Wrong stage type");
//        }
//
//        final var votes = voteMarkerRepository.sumPointsByStageId(stage.getId());
//
//        final double weight = publicStage.getWeight();
//
//        return votes.stream()
//                .collect(Collectors.toMap(
//                        tuple -> tuple.getUserId(),
//                        tuple -> tuple.getPoints() * weight
//                ));
//    }
        return null;
    }
}
