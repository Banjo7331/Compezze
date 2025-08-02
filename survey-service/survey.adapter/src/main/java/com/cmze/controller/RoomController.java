package com.cmze.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequiredArgsConstructor
public class RoomController {

    private final SimpMessagingTemplate messagingTemplate;

    private final Map<String, String> socketRoomMap = new ConcurrentHashMap<>();
    private final Map<String, String> socketUserMap = new ConcurrentHashMap<>();

    @EventListener
    public void handleConnect(SessionConnectedEvent event) {
        String sessionId = StompHeaderAccessor.wrap(event.getMessage()).getSessionId();
        System.out.println("Client connected: " + sessionId);
        messagingTemplate.convertAndSend("/topic/userJoined", "User joined: " + sessionId);
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        String sessionId = StompHeaderAccessor.wrap(event.getMessage()).getSessionId();
        System.out.println("Client disconnected: " + sessionId);

        String roomId = socketRoomMap.remove(sessionId);
        String userId = socketUserMap.remove(sessionId);

        if (roomId != null && userId != null) {
            CreateSurveyRoomResultDto resultDto = new CreateSurveyRoomResultDto(null, null, null);
            surveyRoomService.closeRoom(roomId, userId, resultDto, null);
        }
    }

    @MessageMapping("/roomCreation")
    public void handleRoomCreation(
            @Header("simpSessionId") String sessionId,
            @Payload Map<String, String> payload) {
        String roomId = payload.get("roomId");
        String userId = payload.get("userId");

        socketRoomMap.put(sessionId, roomId);
        socketUserMap.put(sessionId, userId);

        System.out.println("Room created by user " + userId + " for room " + roomId);
    }

    @MessageMapping("/surveyCreation")
    public void handleSurveyCreation(@Payload Map<String, Object> submittedData) {
        messagingTemplate.convertAndSend("/topic/surveyCreated", submittedData);
    }

    @MessageMapping("/surveySubmission")
    public void handleSurveySubmission(@Payload Map<String, Object> submittedData) {
        System.out.println("Survey submitted: " + submittedData);
        messagingTemplate.convertAndSend("/topic/surveySubmitted", submittedData);
    }
}
