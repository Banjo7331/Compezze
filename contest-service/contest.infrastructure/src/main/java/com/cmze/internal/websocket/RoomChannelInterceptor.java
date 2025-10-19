package com.cmze.internal.websocket;

import com.cmze.repository.ParticipantRepository;
import com.cmze.repository.RoomRepository;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
public class RoomChannelInterceptor implements ChannelInterceptor {

    private final RoomRepository roomRepository;
    private final ParticipantRepository participantRepo;

    public RoomChannelInterceptor(RoomRepository roomRepository, ParticipantRepository participantRepo) {
        this.roomRepository = roomRepository;
        this.participantRepo = participantRepo;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        var accessor = MessageHeaderAccessor.getAccessor(message, MessageHeaderAccessor.class);
        if (accessor == null) return message;

        Object cmdObj = accessor.getHeader("stompCommand");
        if (!(cmdObj instanceof StompCommand cmd)) return message;

        // userId z atrybutów sesji (wstawionych w handshake)
        String userId = null;
        Object attrs = accessor.getHeader("simpSessionAttributes");
        if (attrs instanceof Map<?,?> map) {
            Object uid = map.get("userId");
            if (uid != null) userId = uid.toString();
        }

        if (cmd == StompCommand.SUBSCRIBE || cmd == StompCommand.SEND) {
            String dest = (String) accessor.getHeader("simpDestination");
            if (dest == null) return message;

            String roomKey = extractRoomKey(dest);
            if (roomKey != null) {
                var room = liveRoomRepo.findAll().stream()
                        .filter(r -> roomKey.equals(r.getRoomKey()))
                        .findFirst().orElse(null);

                if (room == null || !room.isActive()) throw new AccessControlException("Room not active");

                // użytkownik musi być participantem konkursu (tu najprostsza zasada;
                // doprecyzuj per rola: np. wysyłać mogą tylko ORGANIZER/MODERATOR/HOST)
                boolean isParticipant = (userId != null) &&
                        participantRepo.findByContest_IdAndUserId(room.getContest().getId(), userId).isPresent();

                if (!isParticipant) throw new AccessControlException("Not allowed for this room");
            }
        }
        return message;
    }

    private String extractRoomKey(String destination) {
        // /topic/rooms/{roomKey} lub /app/rooms/{roomKey}/...
        String marker = "/rooms/";
        int idx = destination.indexOf(marker);
        if (idx < 0) return null;
        String tail = destination.substring(idx + marker.length());
        int slash = tail.indexOf('/');
        return (slash < 0) ? tail : tail.substring(0, slash);
    }
}