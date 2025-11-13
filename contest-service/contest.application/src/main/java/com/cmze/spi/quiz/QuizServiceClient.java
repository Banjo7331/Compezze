package com.cmze.spi.quiz;

public interface QuizServiceClient {
    QuizRoomDto createRoom(Long quizFormId, int maxUsers);
}
