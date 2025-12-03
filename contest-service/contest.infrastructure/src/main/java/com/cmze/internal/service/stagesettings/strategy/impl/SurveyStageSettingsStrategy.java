package com.cmze.internal.service.stagesettings.strategy.impl;

import com.cmze.entity.Stage;
import com.cmze.entity.stagesettings.SurveyStage;
import com.cmze.enums.StageType;
import com.cmze.internal.service.stagesettings.strategy.StageSettingsStrategy;
import com.cmze.repository.StageRepository;
import com.cmze.request.StageRequest;
import com.cmze.request.UpdateStageRequest;
import com.cmze.response.stagesettings.StageSettingsResponse;
import com.cmze.spi.survey.SurveyServiceClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@Component
public class SurveyStageSettingsStrategy implements StageSettingsStrategy {
    private final SurveyServiceClient surveyClient;
    private final StageRepository stageRepository;

    public SurveyStageSettingsStrategy(final SurveyServiceClient surveyClient,
                                       final StageRepository stageRepository) {
        this.surveyClient = surveyClient;
        this.stageRepository = stageRepository;
    }

    @Override
    public StageType type() {
        return StageType.SURVEY;
    }

    @Override
    public ProblemDetail validate(final StageRequest dto) {
        if (!(dto instanceof StageRequest.SurveyStageRequest surveyDto)) {
            return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Invalid DTO type for SURVEY");
        }
        if (surveyDto.getSurveyFormId() == null) {
            return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Survey Form ID is required");
        }
        return null;
    }

    @Override
    public Stage createStage(final StageRequest dto) {
        if (dto instanceof StageRequest.SurveyStageRequest req) {
            return SurveyStage.builder()
                    .surveyFormId(req.getSurveyFormId())
                    .maxParticipants(req.getMaxParticipants())
                    .durationMinutes(req.getDurationMinutes())
                    .build();
        }
        throw new IllegalArgumentException("Invalid DTO type for SURVEY strategy");
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
