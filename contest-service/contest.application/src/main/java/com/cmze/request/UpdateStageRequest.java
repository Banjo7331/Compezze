package com.cmze.request;

import com.cmze.enums.StageType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStageRequest {
    @Size(min = 1, max = 100)
    private String name;

    private StageType type;

    @Min(1)
    private Integer durationMinutes;
}
