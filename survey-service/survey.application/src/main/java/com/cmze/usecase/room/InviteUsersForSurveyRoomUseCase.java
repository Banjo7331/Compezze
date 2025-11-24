package com.cmze.usecase.room;

import com.cmze.entity.SurveyRoom;
import com.cmze.repository.SurveyRoomRepository;
import com.cmze.request.GenerateRoomInvitesRequest;
import com.cmze.response.GenerateRoomInvitesResponse;
import com.cmze.shared.ActionResult;
import com.cmze.spi.helpers.invites.SoulboundTokenService;
import com.cmze.usecase.UseCase;
import com.cmze.ws.event.InvitationsGeneratedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@UseCase
public class InviteUsersForSurveyRoomUseCase {

    private final SurveyRoomRepository surveyRoomRepository;
    private final SoulboundTokenService soulboundTokenService;
    private final ApplicationEventPublisher eventPublisher;

    public InviteUsersForSurveyRoomUseCase(
            SurveyRoomRepository surveyRoomRepository,
            SoulboundTokenService soulboundTokenService,
            ApplicationEventPublisher eventPublisher) {
        this.surveyRoomRepository = surveyRoomRepository;
        this.soulboundTokenService = soulboundTokenService;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public ActionResult<GenerateRoomInvitesResponse> execute(UUID roomId, GenerateRoomInvitesRequest targetUserIds, UUID requestingHostId) {
        SurveyRoom room = surveyRoomRepository.findById(roomId).orElse(null);
        if (room == null) {
            return ActionResult.failure(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "Room not found"));
        }
        if (!room.getUserId().equals(requestingHostId)) {
            return ActionResult.failure(ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "Only host can generate invites"));
        }

        Map<UUID, String> generatedTokens = new HashMap<>();
        String surveyTitle = room.getSurvey().getTitle();

        for (UUID targetUserId : targetUserIds.getUserIds()) {
            String token = soulboundTokenService.mintInvitationToken(roomId, targetUserId);
            generatedTokens.put(targetUserId, token);
        }

        eventPublisher.publishEvent(new InvitationsGeneratedEvent(
                this,
                roomId.toString(),
                surveyTitle,
                generatedTokens
        ));
        GenerateRoomInvitesResponse response = new GenerateRoomInvitesResponse(
                generatedTokens
        );

        return ActionResult.success(response);
    }
}
