package com.cmze.internal.service.stagesettings.strategy.impl;

import com.cmze.entity.Stage;
import com.cmze.enums.StageType;
import com.cmze.internal.service.stagesettings.strategy.StageSettingsStrategy;
import com.cmze.request.CreateContestRequest;
import com.cmze.response.stagesettings.StageSettingsResponse;
import org.springframework.http.ProblemDetail;

public class EmptyStageSettingsStrategy implements StageSettingsStrategy {

    private final StageType type;

    public EmptyStageSettingsStrategy(StageType type) {
        this.type = type;
    }

    @Override
    public StageType type() { return type; }

    @Override
    public ProblemDetail validate(CreateContestRequest.StageRequest dto) {
        // opcjonalnie możesz zablokować przesłane settings != null
        return null;
    }

    @Override
    public void apply(CreateContestRequest.StageRequest dto, Stage stage) {
        // nic — brak encji konfiguracyjnej do zapisu
    }

    @Override
    public StageSettingsResponse runStage(long stageId) {
        return null;
    }
}
