package com.cmze.request.stagesettings;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SurveySettingsRequest implements StageSettingsRequest {
    @NotNull
    private Long surveyFormId;

    private Boolean showResultsLive; // null => true
}
