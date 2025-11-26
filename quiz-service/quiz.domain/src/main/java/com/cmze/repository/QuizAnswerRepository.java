package com.cmze.repository;

import com.cmze.entity.QuizAnswer;

import java.util.UUID;

public interface QuizAnswerRepository {
    boolean existsByUserIdAndRoomIdAndQuestionIndex(UUID userId, UUID roomId, int questionIndex);
    QuizAnswer save(QuizAnswer quizAnswer);
}
