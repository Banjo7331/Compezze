package com.cmze.spi.amqp.quiz;

public interface QuizCatalogPort {
    QuizFormMeta getQuizFormMeta(Long quizFormId, String userId);
    record QuizFormMeta(Long id, String name, int questionsCount, Integer defaultDurationMinutes) {}
}
