package com.cmze.usecase;

import com.cmze.entity.SurveyEntrant;
import com.cmze.entity.SurveyForm;
import com.cmze.entity.SurveyRoom;
import com.cmze.repository.SurveyEntrantRepository;
import com.cmze.repository.SurveyRoomRepository;
import com.cmze.request.JoinSurveyRoomRequest;
import com.cmze.response.GetSurveyResponse.GetQuestionResponse;
import com.cmze.response.GetSurveyResponse.GetSurveyFormResponse;
import com.cmze.response.JoinSurveyRoomResponse;
import com.cmze.shared.ActionResult;
import com.cmze.spi.helpers.invites.SoulboundTokenService;
import com.cmze.ws.event.EntrantJoinedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@UseCase
public class JoinSurveyRoomUseCase {
    private static final Logger logger = LoggerFactory.getLogger(JoinSurveyRoomUseCase.class);

    private final SurveyRoomRepository surveyRoomRepository;
    private final SurveyEntrantRepository surveyEntrantRepository;
    private final SoulboundTokenService soulboundTokenService;
    private final ApplicationEventPublisher eventPublisher;

    public JoinSurveyRoomUseCase(SurveyRoomRepository surveyRoomRepository,
                                 SurveyEntrantRepository surveyEntrantRepository,
                                 SoulboundTokenService soulboundTokenService,
                                 ApplicationEventPublisher eventPublisher) {
        this.surveyRoomRepository = surveyRoomRepository;
        this.surveyEntrantRepository = surveyEntrantRepository;
        this.soulboundTokenService = soulboundTokenService;
        this.eventPublisher = eventPublisher;
    }

    public ActionResult<JoinSurveyRoomResponse> execute(UUID roomId, UUID participantUserId, JoinSurveyRoomRequest request) {
        try {
            Optional<SurveyRoom> roomOpt = surveyRoomRepository.findByIdWithSurveyAndQuestions(roomId);
            if (roomOpt.isEmpty()) {
                return ActionResult.failure(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "Room not found."));
            }
            SurveyRoom room = roomOpt.get();

            if (!room.isOpen()) {
                return ActionResult.failure(ProblemDetail.forStatusAndDetail(HttpStatus.GONE, "This room has been closed by the host."));
            }

            String token = (request != null) ? request.getInvitationToken() : null;
            if (room.isPrivate() && !isAccessAllowed(room, participantUserId, token)) {
                return ActionResult.failure(ProblemDetail.forStatusAndDetail(
                        HttpStatus.FORBIDDEN,
                        "This room is private. You need a valid invitation link assigned to your account."
                ));
            }

            Optional<SurveyEntrant> existingEntrantOpt = surveyEntrantRepository
                    .findBySurveyRoom_IdAndParticipantUserId(roomId, participantUserId);

            boolean isHost = room.getUserId().equals(participantUserId);

            if (existingEntrantOpt.isPresent()) {
                SurveyEntrant existingEntrant = existingEntrantOpt.get();

                boolean hasSubmitted = (existingEntrant.getSurveyAttempt() != null);

                JoinSurveyRoomResponse response = new JoinSurveyRoomResponse(
                        existingEntrant.getId(),
                        mapSurveyToDto(room.getSurvey()),
                        hasSubmitted,
                        isHost
                );

                return ActionResult.success(response);
            }

            long currentSize = surveyEntrantRepository.countBySurveyRoom_Id(roomId);
            if (room.getMaxParticipants() != null && currentSize >= room.getMaxParticipants()) {
                return ActionResult.failure(ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, "This room is full."));
            }

            SurveyEntrant newEntrant = new SurveyEntrant();
            newEntrant.setSurveyRoom(room);
            newEntrant.setUserId(participantUserId);

            SurveyEntrant savedParticipant;

            try {
                savedParticipant = surveyEntrantRepository.save(newEntrant);
                logger.info("User {} joined room {} with participant id {}", participantUserId, roomId, savedParticipant.getId());

                long newSize = currentSize + 1;
                eventPublisher.publishEvent(new EntrantJoinedEvent(this, savedParticipant, newSize));
            }catch (DataIntegrityViolationException e) {
                logger.info("Race condition: User {} already in room {}. Retrieving entry.", participantUserId, roomId);
                savedParticipant = surveyEntrantRepository.findBySurveyRoom_IdAndParticipantUserId(roomId, participantUserId)
                        .orElseThrow(() -> new IllegalStateException("DB Error: Duplicate key but record not found."));

            }

            JoinSurveyRoomResponse response = new JoinSurveyRoomResponse(
                    savedParticipant.getId(),
                    mapSurveyToDto(room.getSurvey()),
                    false,
                    isHost
            );

            return ActionResult.success(response);

        } catch (Exception e) {
            logger.error("Failed to join room {}: {}", roomId, e.getMessage(), e);
            return ActionResult.failure(ProblemDetail.forStatusAndDetail(
                    HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred."
            ));
        }
    }

    private boolean isAccessAllowed(SurveyRoom room, UUID participantUserId, String token) {
        if (room.getUserId().equals(participantUserId)) return true;
        if (token != null && !token.isBlank()) {
            return soulboundTokenService.validateSoulboundToken(token, participantUserId, room.getId());
        }
        return false;
    }

    private GetSurveyFormResponse mapSurveyToDto(SurveyForm survey) {
        if (survey == null) return null;

        return new GetSurveyFormResponse(
                survey.getId(),
                survey.getTitle(),
                survey.getQuestions().stream()
                        .map(q -> new GetQuestionResponse(
                                q.getId(),
                                q.getTitle(),
                                q.getType(),
                                new HashSet<>(q.getPossibleChoices())
                        )).collect(Collectors.toList())
        );
    }
}