package com.cmze.entity.stagesettings;

import com.cmze.entity.Stage;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "stage_quiz_config")
public class StageQuizConfig {
    @Id
    @Column(name = "stage_id")
    private Long stageId;

    @OneToOne(fetch = FetchType.LAZY, optional = false) @MapsId
    @JoinColumn(name = "stage_id", nullable = false)
    private Stage stage;

    /** ID istniejącego formularza quizu w mikroserwisie quiz (QuizForm.id) */
    @Column(name = "quiz_form_id", nullable = false)
    private Long quizFormId;

    @Column(name = "weight", nullable = false)
    private double weight = 1.0;
}
