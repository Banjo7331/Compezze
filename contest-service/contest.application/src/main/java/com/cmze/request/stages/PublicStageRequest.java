package com.cmze.request.stages;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PublicStageRequest extends StageRequest {
    @NotNull
    private Double weight = 1.0;

    @NotNull @Min(1)
    private Integer maxScore = 1;
}
