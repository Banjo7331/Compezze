package com.cmze.entity.stagesettings;

import com.cmze.entity.Stage;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "stage_survey_config")
public class StageSurveyConfig {

    @Id
    @Column(name = "stage_id")
    private Long stageId;

    @OneToOne(fetch = FetchType.LAZY, optional = false) @MapsId
    @JoinColumn(name = "stage_id", nullable = false)
    private Stage stage;

    /** ID formularza ankiety w mikroserwisie survey (SurveyForm.id) */
    @Column(name = "survey_form_id", nullable = false)
    private Long surveyFormId;

    /** Czy podczas etapu pokazywać uczestnikom zbiorcze wyniki (live) */
    @Column(name = "show_results_live", nullable = false)
    private boolean showResultsLive = true;
}
