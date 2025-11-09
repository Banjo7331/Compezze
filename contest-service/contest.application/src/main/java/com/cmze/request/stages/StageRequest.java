package com.cmze.request.stages;

import com.cmze.enums.StageType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = JuryStageRequest.class, name = "JURY_VOTE"),
        @JsonSubTypes.Type(value = QuizStageRequest.class, name = "QUIZ"),
        @JsonSubTypes.Type(value = SurveyStageRequest.class, name = "SURVEY"),
        @JsonSubTypes.Type(value = PublicStageRequest.class, name = "PUBLIC_VOTE"),
        @JsonSubTypes.Type(value = GenericStageRequest.class, name = "GENERIC")
})
public abstract class StageRequest {

    @NotBlank
    @Size(max = 100)
    private String name;

    @Min(1)
    private int position;

    @Min(1)
    private int durationMinutes;

    @NotNull(message = "Stage type must not be null")
    private StageType type;
}
