package com.cmze.internal.service.stagesettings.strategy.impl;

import com.cmze.entity.Stage;
import com.cmze.enums.StageType;
import com.cmze.internal.service.stagesettings.strategy.StageSettingsStrategy;
import com.cmze.request.StageRequest;
import com.cmze.request.UpdateStageRequest;
import com.cmze.response.stagesettings.StageSettingsResponse;
import org.springframework.http.ProblemDetail;

import java.util.Map;
import java.util.UUID;

public class EmptyStageSettingsStrategy implements StageSettingsStrategy {

    private final StageType type;

    public EmptyStageSettingsStrategy(StageType type) {
        this.type = type;
    }

    @Override
    public StageType type() { return type; }

    @Override
    public ProblemDetail validate(StageRequest dto) {
        return null;
    }

    @Override
    public Stage createStage(StageRequest dto) {
        return null;
    }

    @Override
    public void updateStage(UpdateStageRequest dto, Stage stage) {

    }
    @Override
    public StageSettingsResponse runStage(long stageId) {
        return null;
    }

    @Override
    public Map<UUID, Double> collectResults(Stage stage) {
        return Map.of();
    }
}
