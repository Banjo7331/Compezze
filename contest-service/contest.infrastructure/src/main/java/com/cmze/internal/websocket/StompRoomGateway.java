package com.cmze.internal.websocket;

import com.cmze.response.event.ChatEvent;
import com.cmze.response.event.RoomEvent;
import com.cmze.spi.RoomGateway;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

@Component
public class StompRoomGateway implements RoomGateway {

    private final SimpMessageSendingOperations messaging;

    public StompRoomGateway(SimpMessageSendingOperations messaging) {
        this.messaging = messaging;
    }

    @Override
    public void publishRoomEvent(RoomEvent event) {
        messaging.convertAndSend("/topic/rooms/" + event.getRoomKey(), event);
    }

    @Override
    public void publishChatEvent(ChatEvent event) {
        messaging.convertAndSend("/topic/rooms/" + event.getRoomKey(), event);
    }
}

