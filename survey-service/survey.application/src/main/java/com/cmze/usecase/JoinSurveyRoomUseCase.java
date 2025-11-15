package com.cmze.usecase;

import com.cmze.entity.SurveyEntrant;
import com.cmze.entity.SurveyRoom;
import com.cmze.repository.SurveyEntrantRepository;
import com.cmze.repository.SurveyRoomRepository;
import com.cmze.response.GetSurveyResponse.GetQuestionResponse;
import com.cmze.response.GetSurveyResponse.GetSurveyFormResponse;
import com.cmze.response.JoinSurveyRoomResponse;
import com.cmze.shared.ActionResult;
import com.cmze.ws.event.EntrantJoinedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@UseCase
public class JoinSurveyRoomUseCase {
    private static final Logger logger = LoggerFactory.getLogger(JoinSurveyRoomUseCase.class);
    private final SurveyRoomRepository surveyRoomRepository;
    private final SurveyEntrantRepository surveyEntrantRepository;
    private final ApplicationEventPublisher eventPublisher;

    public JoinSurveyRoomUseCase(SurveyRoomRepository surveyRoomRepository,
                                 SurveyEntrantRepository surveyEntrantRepository,
                                 ApplicationEventPublisher eventPublisher) {
        this.surveyRoomRepository = surveyRoomRepository;
        this.surveyEntrantRepository = surveyEntrantRepository;
        this.eventPublisher = eventPublisher;
    }

    public ActionResult<JoinSurveyRoomResponse> execute(UUID roomId, UUID participantUserId) {
        try {
            Optional<SurveyRoom> roomOpt = surveyRoomRepository.findById(roomId);
            if (roomOpt.isEmpty()) {
                logger.warn("Join failed: Room {} not found", roomId);
                return ActionResult.failure(ProblemDetail.forStatusAndDetail(
                        HttpStatus.NOT_FOUND, "Room not found."
                ));
            }
            SurveyRoom room = roomOpt.get();

            if (!room.isOpen()) {
                logger.warn("Join failed: Room {} is closed", roomId);
                return ActionResult.failure(ProblemDetail.forStatusAndDetail(
                        HttpStatus.GONE, "This room has been closed by the host."
                ));
            }

            long currentSize = surveyEntrantRepository.countBySurveyRoom_Id(roomId);

            if (room.getMaxParticipants() != null && currentSize >= room.getMaxParticipants()) {
                logger.warn("Join failed: Room {} is full ({} participants)", roomId, currentSize);
                return ActionResult.failure(ProblemDetail.forStatusAndDetail(
                        HttpStatus.CONFLICT, "This room is full."
                ));
            }

            if (surveyEntrantRepository.existsBySurveyRoom_IdAndParticipantUserId(roomId, participantUserId)) {
                logger.warn("Join failed: User {} already in room {}", participantUserId, roomId);
                return ActionResult.failure(ProblemDetail.forStatusAndDetail(
                        HttpStatus.CONFLICT, "You have already joined this room."
                ));
            }

            SurveyEntrant newEntrant = new SurveyEntrant();
            newEntrant.setSurveyRoom(room);
            newEntrant.setUserId(participantUserId);

            SurveyEntrant savedParticipant = surveyEntrantRepository.save(newEntrant);
            logger.info("User {} joined room {} with participant id {}", participantUserId, roomId, savedParticipant.getId());

            long newSize = currentSize + 1;
            eventPublisher.publishEvent(new EntrantJoinedEvent(this, savedParticipant, newSize));

            JoinSurveyRoomResponse response = new JoinSurveyRoomResponse(
                    savedParticipant.getId(),
                    mapSurveyToDto(room.getSurvey())
            );

            return ActionResult.success(response);

        } catch (Exception e) {
            logger.error("Failed to join room {}: {}", roomId, e.getMessage(), e);
            return ActionResult.failure(ProblemDetail.forStatusAndDetail(
                    HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred."
            ));
        }
    }

    private GetSurveyFormResponse mapSurveyToDto(com.cmze.entity.SurveyForm survey) {
        if (survey == null) return null;

        return new GetSurveyFormResponse(
                survey.getId(),
                survey.getTitle(),
                survey.getQuestions().stream().map(q -> new GetQuestionResponse(
                        q.getId(),
                        q.getTitle(),
                        q.getType(),
                        q.getPossibleChoices()
                )).collect(Collectors.toList())
        );
    }
}
