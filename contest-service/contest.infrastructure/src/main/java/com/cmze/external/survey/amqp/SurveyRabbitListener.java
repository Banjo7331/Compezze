//package com.cmze.external.survey.amqp;
//
//import com.cmze.spi.amqp.survey.SurveyEventHandler;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.stereotype.Component;
//
//import java.util.Map;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class SurveyRabbitListener {
//
//    private final SurveyEventHandler handler;
//
//    @RabbitListener(queues = com.cmze.infrastructure.amqp.RabbitConfig.CONTEST_EVT_QUEUE)
//    public void onEvent(Map<String, Object> env,
//                        @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String key) {
//
//        if (!key.startsWith("survey.event.")) return;
//
//        String contestId = (String) env.get("contestId");
//        Long stageId = env.get("stageId") != null ? Long.valueOf(env.get("stageId").toString()) : null;
//        String sessionId = (String) env.get("sessionId");
//        Map<String, Object> payload = (Map<String, Object>) env.get("payload");
//
//        switch (key) {
//            case "survey.event.session.created.v1" -> {
//                String joinHint = payload != null ? (String) payload.getOrDefault("websocketJoinHint", null) : null;
//                handler.onSessionCreated(contestId, stageId, sessionId, joinHint);
//            }
//            case "survey.event.progress.updated.v1" -> {
//                Map<String, Object> stats = payload != null ? (Map<String, Object>) payload.getOrDefault("stats", Map.of()) : Map.of();
//                handler.onProgressUpdated(contestId, stageId, stats);
//            }
//            case "survey.event.results.finalized.v1" ->
//                    handler.onResultsFinalized(contestId, stageId, (Map<String, Integer>) payload.getOrDefault("finalResults", Map.of()));
//            case "survey.event.heartbeat.v1" -> handler.onHeartbeat(contestId, stageId);
//            default -> log.debug("Ignoring {}", key);
//        }
//    }
//}
