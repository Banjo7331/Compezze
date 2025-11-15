package com.cmze.internal.ws.messages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserJoinedSocketMessage {

    private Long participantId;

    private UUID participantUserId;

    private long newParticipantCount;
}
