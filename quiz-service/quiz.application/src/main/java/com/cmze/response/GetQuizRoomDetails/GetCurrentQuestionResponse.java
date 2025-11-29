package com.cmze.response.GetQuizRoomDetails;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetCurrentQuestionResponse {
    private Long questionId;
    private int questionIndex;
    private String title;
    private int timeLimitSeconds;
    private LocalDateTime startTime;
    private List<GetQuestionOptionResponse> options;
}
