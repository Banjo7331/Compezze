package com.cmze.spi.amqp.survey;

import java.util.Map;

public interface SurveyEventHandler {
    void onSessionCreated(String contestId, Long stageId, String sessionId, String joinHint);
    void onResultsFinalized(String contestId, Long stageId, Map<String, Integer> finalResults);
}
