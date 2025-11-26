package com.cmze.internal.scheduler;

import com.cmze.repository.QuizRoomRepository;
import com.cmze.usecase.room.FinishCurrentQuestionUseCase;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class QuizGameLoopScheduler {

    private final QuizRoomRepository quizRoomRepository;
    private final FinishCurrentQuestionUseCase finishCurrentQuestionUseCase;

    public QuizGameLoopScheduler(final QuizRoomRepository quizRoomRepository,
                                 final FinishCurrentQuestionUseCase finishCurrentQuestionUseCase) {
        this.quizRoomRepository = quizRoomRepository;
        this.finishCurrentQuestionUseCase = finishCurrentQuestionUseCase;
    }

    @Scheduled(fixedRate = 1000)
    public void checkQuestionTimers() {
        final var now = LocalDateTime.now();

        final var expiredRooms = quizRoomRepository.findRoomsWithExpiredQuestion(now);

        if (!expiredRooms.isEmpty()) {
            expiredRooms.forEach(finishCurrentQuestionUseCase::executeSystem);
        }
    }
}
