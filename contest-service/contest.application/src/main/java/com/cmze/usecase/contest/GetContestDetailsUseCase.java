package com.cmze.usecase.contest;

import com.cmze.entity.Stage;
import com.cmze.repository.ContestRepository;
import com.cmze.response.GetContestDetailsResponse;
import com.cmze.response.GetStageDetailsResponse;
import com.cmze.shared.ActionResult;
import com.cmze.usecase.UseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.stream.Collectors;

@UseCase
public class GetContestDetailsUseCase {

    private static final Logger logger = LoggerFactory.getLogger(GetContestDetailsUseCase.class);

    private final ContestRepository contestRepository;

    public GetContestDetailsUseCase(final ContestRepository contestRepository) {
        this.contestRepository = contestRepository;
    }

    @Transactional(readOnly = true)
    public ActionResult<GetContestDetailsResponse> execute(final String contestIdString) {
        try {
            // 1. Pobranie Konkursu
            Long contestId = Long.valueOf(contestIdString);

            final var contestOpt = contestRepository.findById(contestId);
            if (contestOpt.isEmpty()) {
                return ActionResult.failure(ProblemDetail.forStatusAndDetail(
                        HttpStatus.NOT_FOUND, "Contest not found"
                ));
            }
            final var contest = contestOpt.get();

            // 2. Mapowanie Etapów (Plan)
            final var stagesDto = contest.getStages().stream()
                    .sorted(Comparator.comparingInt(Stage::getPosition))
                    .map(s -> new GetStageDetailsResponse(
                            s.getId(),
                            s.getName(),
                            s.getType(),
                            s.getDurationMinutes(),
                            s.getPosition()
                    ))
                    .collect(Collectors.toList());

            // 3. Budowanie Response (Czysta definicja)
            final var response = new GetContestDetailsResponse(
                    contest.getId().toString(),
                    contest.getName(),
                    contest.getDescription(),
                    contest.getLocation(),
                    contest.getContestCategory(),
                    contest.getStartDate(),
                    contest.getEndDate(),
                    contest.getStatus(),
                    contest.getParticipantLimit() != null ? contest.getParticipantLimit() : 0,
                    contest.isPrivate(),
                    stagesDto
            );

            return ActionResult.success(response);

        } catch (NumberFormatException e) {
            return ActionResult.failure(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Invalid ID format"));
        } catch (Exception e) {
            logger.error("Failed to get contest details {}", contestIdString, e);
            return ActionResult.failure(ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Error"));
        }
    }
}
