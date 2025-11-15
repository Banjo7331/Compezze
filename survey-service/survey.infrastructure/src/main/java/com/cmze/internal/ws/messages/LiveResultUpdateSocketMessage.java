package com.cmze.internal.ws.messages;

import com.cmze.internal.ws.FinalRoomResultDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class LiveResultUpdateSocketMessage {
    private String event = "LIVE_RESULTS_UPDATE";
    private FinalRoomResultDto currentResults;

    public LiveResultUpdateSocketMessage(FinalRoomResultDto currentResults) {
        this.currentResults = currentResults;
    }
}
