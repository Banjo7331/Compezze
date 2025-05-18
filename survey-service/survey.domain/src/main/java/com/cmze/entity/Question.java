package com.cmze.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "survey-questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String type;

    @ElementCollection
    @CollectionTable(name = "survey_possible_choices", joinColumns = @JoinColumn(name = "question_id"))
    @Column(name = "possiblechoice")
    private List<String> possibleChoices;

    @ManyToOne
    @JoinColumn(name = "survey_id", nullable = false)
    private SurveyForm surveyForm;
}
