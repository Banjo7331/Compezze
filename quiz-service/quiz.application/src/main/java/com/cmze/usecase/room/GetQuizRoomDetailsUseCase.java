package com.cmze.usecase.room;

import com.cmze.enums.QuizRoomStatus;
import com.cmze.repository.QuizEntrantRepository;
import com.cmze.repository.QuizRoomRepository;
import com.cmze.response.GetQuizRoomDetailsResponse;
import com.cmze.shared.ActionResult;
import com.cmze.spi.helpers.room.QuizResultCounter;
import com.cmze.usecase.UseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@UseCase
public class GetQuizRoomDetailsUseCase {

    private static final Logger logger = LoggerFactory.getLogger(GetQuizRoomDetailsUseCase.class);

    private final QuizRoomRepository quizRoomRepository;
    private final QuizEntrantRepository quizEntrantRepository;
    private final QuizResultCounter quizResultCounter;

    public GetQuizRoomDetailsUseCase(final QuizRoomRepository quizRoomRepository,
                                     final QuizEntrantRepository quizEntrantRepository,
                                     final QuizResultCounter quizResultCounter) {
        this.quizRoomRepository = quizRoomRepository;
        this.quizEntrantRepository = quizEntrantRepository;
        this.quizResultCounter = quizResultCounter;
    }

    @Transactional(readOnly = true)
    public ActionResult<GetQuizRoomDetailsResponse> execute(final UUID roomId) {
        try {
            final var roomOpt = quizRoomRepository.findByIdWithQuiz(roomId);

            if (roomOpt.isEmpty()) {
                logger.warn("Quiz room details not found for id: {}", roomId);
                return ActionResult.failure(ProblemDetail.forStatusAndDetail(
                        HttpStatus.NOT_FOUND, "Room not found"
                ));
            }

            final var room = roomOpt.get();

            final var results = quizResultCounter.calculate(room.getId());

            final long participantsCount = quizEntrantRepository.countByQuizRoom_Id(roomId);

            final var response = new GetQuizRoomDetailsResponse(
                    room.getId(),
                    room.getQuiz().getTitle(),
                    room.getHostId(),
                    room.getStatus(),
                    room.isPrivate(),
                    participantsCount,
                    results
            );

            return ActionResult.success(response);

        } catch (Exception e) {
            logger.error("Failed to fetch details for quiz room {}: {}", roomId, e.getMessage(), e);
            return ActionResult.failure(ProblemDetail.forStatusAndDetail(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "An unexpected error occurred while loading room details."
            ));
        }
    }
}
