package com.cmze.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateSurveyRoomResponse {

    private UUID roomId;
    private UUID hostId;
    private Long surveyFormId;
    private Integer maxParticipants;
}
