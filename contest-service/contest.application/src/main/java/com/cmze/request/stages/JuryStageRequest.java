package com.cmze.request.stages;

import com.cmze.enums.JuryRevealMode;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class JuryStageRequest extends StageRequest {

    @NotNull
    private Double weight = 1.0;

    @NotNull @Min(1)
    private Integer maxScore = 10;

    @NotNull
    private JuryRevealMode juryRevealMode = JuryRevealMode.IMMEDIATE;

    @NotNull
    private Boolean showJudgeNames = true;
}
