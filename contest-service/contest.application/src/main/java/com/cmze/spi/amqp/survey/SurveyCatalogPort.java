package com.cmze.spi.amqp.survey;

public interface SurveyCatalogPort {
    SurveyFormMeta getSurveyFormMeta(Long surveyFormId, String userId);
    record SurveyFormMeta(Long id, String title, int questionsCount, Integer defaultDurationMinutes) {}
}
