package com.cmze.internal.ws;

import com.cmze.entity.SurveyEntrant;
import com.cmze.entity.SurveyRoom;
import com.cmze.internal.ws.messages.InvitationSocketMessage;
import com.cmze.spi.helpers.room.FinalRoomResultDto;
import com.cmze.ws.event.EntrantJoinedEvent;
import com.cmze.ws.event.InvitationsGeneratedEvent;
import com.cmze.ws.event.RoomClosedEvent;
import com.cmze.ws.event.SurveyAttemptSubmittedEvent;
import com.cmze.internal.ws.messages.LiveResultUpdateSocketMessage;
import com.cmze.internal.ws.messages.RoomClosedSocketMessage;
import com.cmze.internal.ws.messages.UserJoinedSocketMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SurveyEventWebSocketNotifier {

    private static final Logger logger = LoggerFactory.getLogger(SurveyEventWebSocketNotifier.class);

    private final SimpMessagingTemplate messagingTemplate;
    private final SurveyResultCounterImpl resultsCounter;

    public SurveyEventWebSocketNotifier(SimpMessagingTemplate messagingTemplate,
                                        SurveyResultCounterImpl resultsCounter) {
        this.messagingTemplate = messagingTemplate;
        this.resultsCounter = resultsCounter;
    }

    @EventListener
    public void handleParticipantJoined(EntrantJoinedEvent event) {
        SurveyEntrant participant = event.getParticipant();
        UUID roomId = participant.getSurveyRoom().getId();
        String topic = "/topic/room/" + roomId;

        UserJoinedSocketMessage payload = new UserJoinedSocketMessage(
                participant.getId(),
                participant.getUserId(),
                event.getNewParticipantCount()
        );

        logger.info("Sending USER_JOINED to {}: userId {}", topic, participant.getUserId());
        messagingTemplate.convertAndSend(topic, payload);
    }


    @EventListener
    public void handleSurveySubmitted(SurveyAttemptSubmittedEvent event) {
        UUID roomId = event.getSurveyAttempt().getParticipant().getSurveyRoom().getId();
        String topic = "/topic/room/" + roomId;

        FinalRoomResultDto liveResults = resultsCounter.calculate(roomId);

        LiveResultUpdateSocketMessage payload = new LiveResultUpdateSocketMessage(liveResults);

        logger.info("Sending LIVE_RESULTS_UPDATE to {}: {} submissions", topic, liveResults.getTotalSubmissions());
        messagingTemplate.convertAndSend(topic, payload);
    }

    @EventListener
    public void handleRoomClosed(RoomClosedEvent event) {
        UUID roomId = event.getRoom().getId();
        String topic = "/topic/room/" + roomId;

        FinalRoomResultDto finalResults = resultsCounter.calculate(roomId);

        RoomClosedSocketMessage payload = new RoomClosedSocketMessage(finalResults);

        logger.info("Sending ROOM_CLOSED to {}: {} participants", topic, finalResults.getTotalParticipants());
        messagingTemplate.convertAndSend(topic, payload);
    }

    @EventListener
    public void handleInvitationsGenerated(InvitationsGeneratedEvent event) {
        logger.info("Processing generated invitations for room {}", event.getRoomId());

        event.getInvitations().forEach((userId, token) -> {

            InvitationSocketMessage payload = new InvitationSocketMessage(
                    event.getRoomId(),
                    event.getSurveyTitle(),
                    token
            );

            messagingTemplate.convertAndSendToUser(
                    userId.toString(),
                    "/queue/invitations",
                    payload
            );
        });
    }
}
