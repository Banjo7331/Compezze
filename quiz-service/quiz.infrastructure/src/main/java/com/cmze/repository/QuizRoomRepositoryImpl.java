package com.cmze.repository;

import com.cmze.entity.QuizRoom;
import com.cmze.external.jpa.QuizRoomJpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class QuizRoomRepositoryImpl implements QuizRoomRepository {

    private final QuizRoomJpaRepository impl;

    public QuizRoomRepositoryImpl(QuizRoomJpaRepository impl) {
        this.impl = impl;
    }

    @Override
    public boolean existsActiveRoomsForQuiz(Long quizFormId) {
        return impl.existsActiveRoomsForQuiz(quizFormId);
    }

    @Override
    public Optional<QuizRoom> findByIdWithQuiz(UUID id) {
        return impl.findByIdWithQuiz(id);
    }

    @Override
    public QuizRoom save(QuizRoom quizRoom) {
        return impl.save(quizRoom);
    }

    @Override
    public Optional<QuizRoom> findByIdWithFullQuizStructure(UUID id) {
        return impl.findByIdWithFullQuizStructure(id);
    }

    @Override
    public List<QuizRoom> findRoomsWithExpiredQuestion(LocalDateTime now) {
        return impl.findRoomsWithExpiredQuestion(now);
    }
}
