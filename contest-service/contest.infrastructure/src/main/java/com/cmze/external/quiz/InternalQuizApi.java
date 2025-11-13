package com.cmze.external.quiz;

import com.cmze.configuration.FeignConfig;
import com.cmze.spi.quiz.QuizRoomDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(
        name = "quiz-service",
        path = "/quiz",
        configuration = FeignConfig.class
)
public interface InternalQuizApi {

    @PostMapping("/{quizFormId}/{maxUsers}/create-room")
    ResponseEntity<QuizRoomDto> createRoom(
            @PathVariable("quizFormId") Long quizFormId,
            @PathVariable("maxUsers") int maxUsers
    );
}
