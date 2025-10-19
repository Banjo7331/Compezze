package com.cmze.response.stagesettings;

import com.cmze.enums.JuryRevealMode;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

public class JuryVotingSettingsResponse implements StageSettingsResponse {

    @DecimalMin(value = "0.0", inclusive = false)
    private Double weight; // null ⇒ 1.0

    @Min(1)
    private Integer maxScore; // null ⇒ 10

    @NotNull
    private JuryRevealMode juryRevealMode; // IMMEDIATE / ON_ENTRY_ADVANCE

    private Boolean showJudgeNames; // null ⇒ true
}
