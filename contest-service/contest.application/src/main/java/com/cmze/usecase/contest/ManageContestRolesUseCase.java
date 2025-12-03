package com.cmze.usecase.contest;

import com.cmze.enums.ContestRole;
import com.cmze.repository.ContestRepository;
import com.cmze.repository.ParticipantRepository;
import com.cmze.request.ManageRoleRequest;
import com.cmze.shared.ActionResult;
import com.cmze.usecase.UseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@UseCase
public class ManageContestRolesUseCase {

    private final ContestRepository contestRepository;
    private final ParticipantRepository participantRepository;

    public ManageContestRolesUseCase(final ContestRepository contestRepository,
                                     final ParticipantRepository participantRepository) {
        this.contestRepository = contestRepository;
        this.participantRepository = participantRepository;
    }

    @Transactional
    public ActionResult<Void> execute(final Long contestId,
                                      final UUID organizerId,
                                      final ManageRoleRequest request) {

        final var contest = contestRepository.findById(contestId).orElseThrow();

        if (!contest.getOrganizerId().equals(organizerId.toString())) {
            return ActionResult.failure(ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "Only organizer can manage roles"));
        }

        final var participantOpt = participantRepository.findByContestIdAndUserId(contestId, request.getTargetUserId().toString());

        if (participantOpt.isEmpty()) {
            return ActionResult.failure(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "User is not a participant"));
        }

        final var participant = participantOpt.get();

        if (request.isAssign()) {
            participant.getRoles().add(request.getRole());
        } else {
            participant.getRoles().remove(request.getRole());
        }

        participantRepository.save(participant);

        return ActionResult.success(null);
    }
}
