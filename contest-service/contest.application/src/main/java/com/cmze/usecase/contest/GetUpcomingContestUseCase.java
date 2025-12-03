package com.cmze.usecase.contest;

import com.cmze.repository.ContestRepository;
import com.cmze.response.GetContestSummaryResponse;
import com.cmze.shared.ActionResult;
import com.cmze.usecase.UseCase;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@UseCase
public class GetUpcomingContestUseCase {

    private final ContestRepository contestRepository;

    public GetUpcomingContestUseCase(final ContestRepository contestRepository) {
        this.contestRepository = contestRepository;
    }

    @Transactional(readOnly = true)
    public ActionResult<GetContestSummaryResponse> execute(final UUID userId) {
        try {
            final var contests = contestRepository.findUpcomingForUser(
                    userId.toString(),
                    PageRequest.of(0, 1)
            );

            if (contests.isEmpty()) {
                return ActionResult.success(null);
            }

            final var contest = contests.get(0);
            final boolean isOrganizer = contest.getOrganizerId().equals(userId.toString());

            final var response = new GetContestSummaryResponse(
                    contest.getId().toString(),
                    contest.getName(),
                    contest.getContestCategory(),
                    contest.getStartDate(),
                    contest.getStatus(),
                    isOrganizer
            );

            return ActionResult.success(response);

        } catch (Exception e) {
            return ActionResult.failure(ProblemDetail.forStatusAndDetail(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Error fetching upcoming contest"
            ));
        }
    }
}
