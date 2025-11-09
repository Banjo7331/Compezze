package com.cmze.request.stages;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class QuizStageRequest extends StageRequest {
    @NotNull
    private Long quizFormId;

    @NotNull
    private Double weight = 1.0;
}
