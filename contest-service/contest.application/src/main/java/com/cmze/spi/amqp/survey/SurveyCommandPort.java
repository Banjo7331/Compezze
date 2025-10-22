package com.cmze.spi.amqp.survey;

public interface SurveyCommandPort {
    void startSurvey(String contestId, Long stageId, Long surveyFormId, int durationMinutes, boolean showResultsLive);
}