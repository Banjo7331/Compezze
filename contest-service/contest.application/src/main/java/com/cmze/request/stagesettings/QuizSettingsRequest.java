package com.cmze.request.stagesettings;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizSettingsRequest {
    @NotNull
    private Long quizFormId;

    @DecimalMin("0.0") @DecimalMax("1.0")
    private Double weight; // null => 1.
}
