package com.cmze.external.quiz.amqp;

import com.cmze.spi.amqp.quiz.QuizCommandPort;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
class QuizPublisherRabbit implements QuizPublisher {
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publish(ContestCommandEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitNames.CONTEST_CMD_EXCHANGE,
                RabbitNames.RK_QUIZ_CMD,
                event,
                msg -> {
                    var props = msg.getMessageProperties();
                    props.setCorrelationId(event.getCorrelationId());
                    props.setHeader("contestId", event.getContestId());
                    props.setHeader("stageId", event.getStageId());
                    return msg;
                }
        );
    }
}
