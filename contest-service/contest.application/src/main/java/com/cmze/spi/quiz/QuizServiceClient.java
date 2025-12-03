package com.cmze.spi.quiz;

import com.cmze.spi.quiz.dto.CreateQuizRoomRequest;
import com.cmze.spi.quiz.dto.CreateQuizRoomResponse;
import com.cmze.spi.quiz.dto.GenerateQuizTokenRequest;
import com.cmze.spi.quiz.dto.GenerateQuizTokenResponse;

public interface QuizServiceClient {
    CreateQuizRoomResponse createRoom(CreateQuizRoomRequest request);
    GenerateQuizTokenResponse generateToken(String roomId, GenerateQuizTokenRequest request);
//    QuizRoomDetailsDto getRoomDetails(String roomId);
}
