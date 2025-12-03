package com.cmze.internal.service.stagesettings.strategy.impl;

import com.cmze.entity.Stage;
import com.cmze.entity.stagesettings.JuryVoteStage;
import com.cmze.enums.StageType;
import com.cmze.internal.service.stagesettings.strategy.StageSettingsStrategy;
import com.cmze.repository.StageRepository;
import com.cmze.request.StageRequest;
import com.cmze.request.UpdateStageRequest;
import com.cmze.response.stagesettings.StageSettingsResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@Component
public class JuryStageSettingsStrategy implements StageSettingsStrategy {

    private final StageRepository stageRepository;

    public JuryStageSettingsStrategy(final StageRepository stageRepository) {
        this.stageRepository = stageRepository;
    }

    @Override
    public StageType type() {
        return StageType.JURY_VOTE;
    }

    @Override
    public ProblemDetail validate(final StageRequest dto) {
        if (!(dto instanceof StageRequest.JuryStageRequest)) {
            return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Invalid DTO type for JURY strategy");
        }
        return null;
    }

    @Override
    public Stage createStage(final StageRequest dto) {
        if (dto instanceof StageRequest.JuryStageRequest req) {
            return JuryVoteStage.builder()
                    .weight(req.getWeight())
                    .maxScore(req.getMaxScore())
                    .juryRevealMode(req.getJuryRevealMode())
                    .showJudgeNames(req.getShowJudgeNames())
                    .build();
        }
        throw new IllegalArgumentException("Invalid DTO for JURY strategy");
    }

    @Override
    public void updateStage(UpdateStageRequest dto, Stage stage) {

    }

    @Override
    public StageSettingsResponse runStage(final long stageId) {
        return null;
    }

    @Override
    public Map<UUID, Double> collectResults(final Stage stage) {
        return Collections.emptyMap();
    }
}
