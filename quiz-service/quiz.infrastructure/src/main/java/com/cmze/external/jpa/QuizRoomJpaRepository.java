package com.cmze.external.jpa;

import com.cmze.entity.QuizRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QuizRoomJpaRepository extends JpaRepository<QuizRoom, UUID> {

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END " +
            "FROM QuizRoom r " +
            "WHERE r.quiz.id = :quizFormId AND r.status != 'FINISHED'")
    boolean existsActiveRoomsForQuiz(@Param("quizFormId") Long quizFormId);

    @Query("SELECT r FROM QuizRoom r LEFT JOIN FETCH r.quiz WHERE r.id = :id")
    Optional<QuizRoom> findByIdWithQuiz(@Param("id") UUID id);

    @Query("SELECT r FROM QuizRoom r " +
            "LEFT JOIN FETCH r.quiz q " +
            "LEFT JOIN FETCH q.questions qs " +
            "LEFT JOIN FETCH qs.options " +
            "WHERE r.id = :id")
    Optional<QuizRoom> findByIdWithFullQuizStructure(@Param("id") UUID id);

    @Query("SELECT r FROM QuizRoom r " +
            "LEFT JOIN FETCH r.quiz q " +
            "LEFT JOIN FETCH q.questions qs " +
            "LEFT JOIN FETCH qs.options " +
            "WHERE r.status = 'QUESTION_ACTIVE' AND r.currentQuestionEndTime < :now")
    List<QuizRoom> findRoomsWithExpiredQuestion(@Param("now") LocalDateTime now);
}
