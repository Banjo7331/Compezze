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
@Table(name = "stage_quiz")
@DiscriminatorValue("QUIZ")
public class QuizStage extends Stage {

    @Column(name = "quiz_form_id", nullable = false)
    private Long quizFormId;

    @Column(name = "weight", nullable = false)
    private double weight = 1.0;

}
