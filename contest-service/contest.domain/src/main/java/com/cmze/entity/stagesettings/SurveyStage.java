package com.cmze.entity.stagesettings;

import com.cmze.entity.Stage;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "stage_survey")
@DiscriminatorValue("SURVEY")
public class SurveyStage extends Stage {

    @Column(name = "survey_form_id", nullable = false)
    private Long surveyFormId;

    @Column(name = "show_results_live", nullable = false)
    private boolean showResultsLive = true;

}
