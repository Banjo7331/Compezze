package com.cmze.response.stagesettings;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
        property = "type",
        visible = true,
        defaultImpl = EmptySettingsResponse.class
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PublicVotingSettingsResponse.class, name = "PUBLIC_VOTING"),
        @JsonSubTypes.Type(value = JuryVotingSettingsResponse.class,   name = "JURY_VOTING"),
})
public interface StageSettingsResponse { }
