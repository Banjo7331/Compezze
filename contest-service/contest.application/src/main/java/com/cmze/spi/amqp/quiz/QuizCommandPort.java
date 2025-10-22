package com.cmze.spi.amqp.quiz;

public interface QuizCommandPort {
    void startQuiz(String contestId, Long stageId, Long quizFormId, int durationMinutes, double weight);
}
