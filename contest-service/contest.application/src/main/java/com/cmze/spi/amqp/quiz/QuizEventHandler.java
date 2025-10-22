package com.cmze.spi.amqp.quiz;

import java.util.Map;

public interface QuizEventHandler {
    void onSessionCreated(String contestId, Long stageId, String sessionId, String joinHint);
    void onResultsFinalized(String contestId, Long stageId, Map<String, Integer> finalResults);
}
