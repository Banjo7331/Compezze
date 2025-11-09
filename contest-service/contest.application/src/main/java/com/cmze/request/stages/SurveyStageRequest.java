package com.cmze.request.stages;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SurveyStageRequest extends StageRequest {
    @NotNull
    private Long surveyFormId;

    @NotNull
    private Boolean showResultsLive = true;
}
