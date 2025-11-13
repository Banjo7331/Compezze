package com.cmze.external.quiz;

import com.cmze.spi.quiz.QuizRoomDto;
import com.cmze.spi.quiz.QuizServiceClient;
import feign.FeignException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class QuizServiceClientImpl implements QuizServiceClient {

    private final InternalQuizApi internalApi;

    public QuizServiceClientImpl(InternalQuizApi internalApi) {
        this.internalApi = internalApi;
    }

    @Override
    public QuizRoomDto createRoom(Long quizFormId, int maxUsers) {
        try {
            ResponseEntity<QuizRoomDto> response = internalApi.createRoom(quizFormId, maxUsers);
            return response.getBody();
        } catch (FeignException.BadRequest | FeignException.NotFound ex) {
            throw new RuntimeException("Failed to create quiz room: " + ex.getMessage(), ex);
        } catch (Exception e) {
            throw new RuntimeException("Quiz service is unavailable or failed: " + e.getMessage(), e);
        }
    }
}
