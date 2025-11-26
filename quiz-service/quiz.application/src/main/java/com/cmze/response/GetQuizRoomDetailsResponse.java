package com.cmze.response;

import com.cmze.enums.QuizRoomStatus;
import com.cmze.spi.helpers.room.FinalRoomResultsDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetQuizRoomDetailsResponse {
    private UUID roomId;
    private String quizTitle;
    private UUID hostId;
    private QuizRoomStatus status;
    private boolean isPrivate;
    private long currentParticipants;
    private FinalRoomResultsDto currentResults;
}
