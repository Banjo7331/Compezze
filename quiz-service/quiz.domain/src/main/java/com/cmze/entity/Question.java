package com.cmze.entity;

import com.cmze.enums.QuestionLevel;
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
@Table(name = "quiz-questions")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private QuestionLevel level;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Integer points;

    @ElementCollection
    @CollectionTable(name = "question_possible_answers", joinColumns = @JoinColumn(name = "question_id"))
    @Column(name = "possibleanswer")
    private List<String> possibleAnswers;

    @ElementCollection
    @CollectionTable(name = "question_correct_answers", joinColumns = @JoinColumn(name = "question_id"))
    @Column(name = "correctanswer", nullable = false)
    private List<String> correctAnswer;

    @ManyToOne
    @JoinColumn(name = "quiz_id", nullable = false)
    private QuizForm quizForm;
}
