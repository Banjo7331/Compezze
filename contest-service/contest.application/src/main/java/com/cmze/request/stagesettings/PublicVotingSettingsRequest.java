package com.cmze.request.stagesettings;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PublicVotingSettingsRequest implements StageSettingsRequest {
    @DecimalMin(value = "0.0", inclusive = false)
    private Double weight; // null ⇒ 1.0

    @Min(1)
    private Integer maxScore; // null ⇒ 10
}
