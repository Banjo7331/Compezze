package com.cmze.external.quiz;

import com.cmze.configuration.FeignConfig;
import com.cmze.spi.quiz.dto.CreateQuizRoomRequest;
import com.cmze.spi.quiz.dto.CreateQuizRoomResponse;
import com.cmze.spi.quiz.dto.GenerateQuizTokenRequest;
import com.cmze.spi.quiz.dto.GenerateQuizTokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "quiz-service",
        path = "/api/v1/quiz/room",
        configuration = FeignConfig.class
)
public interface InternalQuizApi {

    @PostMapping
    CreateQuizRoomResponse createRoom(@RequestBody CreateQuizRoomRequest request);

    @PostMapping("/{roomId}/generate-token")
    GenerateQuizTokenResponse generateToken(
            @PathVariable("roomId") String roomId,
            @RequestBody GenerateQuizTokenRequest request
    );

//    @GetMapping("/api/v1/quiz/room/{roomId}")
//    GetQuizRoomDetailsResponse getRoomDetails(@PathVariable("roomId") String roomId);
}
