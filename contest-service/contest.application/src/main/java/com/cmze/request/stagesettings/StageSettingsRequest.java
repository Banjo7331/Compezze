package com.cmze.request.stagesettings;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Polimorficzne ustawienia etapu. Discriminator brany z pola "type" w StageRequest
 * (EXTERNAL_PROPERTY = StageRequest.type).
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
        property = "type",
        visible = true,
        defaultImpl = EmptySettingsRequest.class
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PublicVotingSettingsRequest.class, name = "PUBLIC_VOTING"),
        @JsonSubTypes.Type(value = JuryVotingSettingsRequest.class,   name = "JURY_VOTING"),
        @JsonSubTypes.Type(value = SurveySettingsRequest.class,   name = "SURVEY"),
        @JsonSubTypes.Type(value = QuizSettingsRequest.class,         name = "QUIZ")
})
public interface StageSettingsRequest {}
