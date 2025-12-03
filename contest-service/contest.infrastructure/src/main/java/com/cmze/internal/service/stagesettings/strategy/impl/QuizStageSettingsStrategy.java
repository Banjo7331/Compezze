package com.cmze.internal.service.stagesettings.strategy.impl;

import com.cmze.entity.Stage;
import com.cmze.entity.stagesettings.QuizStage;
import com.cmze.enums.StageType;
import com.cmze.internal.service.stagesettings.strategy.StageSettingsStrategy;
import com.cmze.request.StageRequest;
import com.cmze.request.UpdateStageRequest;
import com.cmze.response.stagesettings.StageSettingsResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class QuizStageSettingsStrategy implements StageSettingsStrategy {

    public QuizStageSettingsStrategy(){

    }

    @Override
    public StageType type() {
        return StageType.QUIZ;
    }

    @Override
    public ProblemDetail validate(final StageRequest dto) {
        if (!(dto instanceof StageRequest.QuizStageRequest surveyDto)) {
            return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Invalid DTO type for QUIZ");
        }
        if (surveyDto.getQuizFormId() == null) {
            return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "QUIZ Form ID is required");
        }
        return null;
    }

    @Override
    public Stage createStage(final StageRequest dto) {
        if (dto instanceof StageRequest.QuizStageRequest quizDto) {
            return QuizStage.builder()
                    .quizFormId(quizDto.getQuizFormId())
                    .weight(quizDto.getWeight())
                    .maxParticipants(quizDto.getMaxParticipants())
                    .timePerQuestion(quizDto.getTimePerQuestion())
                    .build();
        }
        throw new IllegalArgumentException("Invalid DTO for Quiz Strategy");
    }

    @Override
    public void updateStage(UpdateStageRequest dto, Stage stage) {
        if (!(stage instanceof QuizStage quizStage)) throw new IllegalStateException("Type mismatch");
        if (!(dto instanceof UpdateStageRequest.UpdateQuizStageRequest req)) throw new IllegalArgumentException("DTO mismatch");

        if (req.getQuizFormId() != null) quizStage.setQuizFormId(req.getQuizFormId());
        if (req.getWeight() != null) quizStage.setWeight(req.getWeight());
        if (req.getMaxParticipants() != null) quizStage.setMaxParticipants(req.getMaxParticipants());
        if (req.getTimePerQuestion() != null) quizStage.setTimePerQuestion(req.getTimePerQuestion());

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
