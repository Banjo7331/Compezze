package com.cmze.internal.ws;

import com.cmze.repository.QuizEntrantRepository;
import com.cmze.spi.helpers.room.FinalRoomResultsDto;
import com.cmze.spi.helpers.room.LeaderboardEntryDto;
import com.cmze.spi.helpers.room.QuizResultCounter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class QuizResultCounterImpl implements QuizResultCounter {

    private final QuizEntrantRepository quizEntrantRepository;

    public QuizResultCounterImpl(final QuizEntrantRepository quizEntrantRepository) {
        this.quizEntrantRepository = quizEntrantRepository;
    }

    @Override
    public FinalRoomResultsDto calculate(final UUID roomId) {
        final var participants = quizEntrantRepository.findAllByQuizRoom_IdOrderByTotalScoreDesc(roomId);

        final var leaderboard = new ArrayList<LeaderboardEntryDto>();
        int rank = 1;

        for (final var entrant : participants) {
            leaderboard.add(new LeaderboardEntryDto(
                    entrant.getUserId(),
                    entrant.getNickname(),
                    entrant.getTotalScore(),
                    rank++
            ));
        }

        return new FinalRoomResultsDto(
                (long) participants.size(),
                leaderboard
        );
    }
}
