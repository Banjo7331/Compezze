package com.cmze.internal.ws.messages;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class NewQuestionSocketMessage {
    private final String event = "NEW_QUESTION";

    private int questionIndex;
    private String title;
    private List<String> options;
    private int timeLimitSeconds;
    private LocalDateTime startTime;

    public NewQuestionSocketMessage(int questionIndex, String title, List<String> options, int timeLimitSeconds, LocalDateTime startTime) {
        this.questionIndex = questionIndex;
        this.title = title;
        this.options = options;
        this.timeLimitSeconds = timeLimitSeconds;
        this.startTime = startTime;
    }
}
