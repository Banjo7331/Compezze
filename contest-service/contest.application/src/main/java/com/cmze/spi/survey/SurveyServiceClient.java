package com.cmze.spi.survey;

public interface SurveyServiceClient {
    SurveyRoomDto createRoom(Long quizFormId, int maxUsers);
}
