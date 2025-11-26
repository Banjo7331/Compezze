package com.cmze.repository;

import com.cmze.entity.QuizRoom;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QuizRoomRepository {
    boolean existsActiveRoomsForQuiz(Long quizFormId);
    Optional<QuizRoom> findByIdWithQuiz(UUID id);
    QuizRoom save(QuizRoom quizRoom);
    Optional<QuizRoom> findByIdWithFullQuizStructure(UUID id);
    List<QuizRoom> findRoomsWithExpiredQuestion(LocalDateTime now);
}
