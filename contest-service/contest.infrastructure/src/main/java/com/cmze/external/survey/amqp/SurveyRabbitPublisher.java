package com.cmze.external.survey.amqp;

import com.cmze.spi.amqp.survey.SurveyCommandPort;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Component
public class SurveyRabbitPublisher implements SurveyCommandPort {
    private final RabbitTemplate rabbit;
    @Value("${app.amqp.exchange}") private String exchange;

    public SurveyRabbitPublisher(RabbitTemplate rabbit) {
        this.rabbit = rabbit;
    }

    @Override
    public void startSurvey(String contestId, Long stageId, Long surveyFormId, int durationMinutes, boolean showResultsLive) {
        Map<String, Object> env = Map.of(
                "messageId", UUID.randomUUID().toString(),
                "occurredAt", Instant.now().toString(),
                "version", 1,
                "contestId", contestId,
                "stageId", stageId,
                "payload", Map.of(
                        "surveyFormId", surveyFormId,
                        "durationMinutes", durationMinutes,
                        "showResultsLive", showResultsLive
                )
        );
        rabbit.convertAndSend(exchange, "contest.command.survey.start.v1", env);
    }
}
