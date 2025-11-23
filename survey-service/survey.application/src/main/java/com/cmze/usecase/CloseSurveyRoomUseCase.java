package com.cmze.usecase;

import com.cmze.entity.SurveyRoom;
import com.cmze.repository.SurveyRoomRepository;
import com.cmze.shared.ActionResult;
import com.cmze.ws.event.RoomClosedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@UseCase
public class CloseSurveyRoomUseCase {

    private static final Logger logger = LoggerFactory.getLogger(CloseSurveyRoomUseCase.class);
    private final SurveyRoomRepository surveyRoomRepository;
    private final ApplicationEventPublisher eventPublisher;

    public CloseSurveyRoomUseCase(SurveyRoomRepository surveyRoomRepository,
                                  ApplicationEventPublisher eventPublisher) {
        this.surveyRoomRepository = surveyRoomRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public ActionResult<?> execute(UUID roomId, UUID hostUserId) {
        try {
            Optional<SurveyRoom> roomOpt = surveyRoomRepository.findById(roomId);
            if (roomOpt.isEmpty()) {
                logger.warn("Close failed: Room {} not found", roomId);
                return ActionResult.failure(ProblemDetail.forStatusAndDetail(
                        HttpStatus.NOT_FOUND, "Room not found."
                ));
            }
            SurveyRoom room = roomOpt.get();

            if (!room.getUserId().equals(hostUserId)) {
                logger.warn("Close failed: User {} is not host of room {}", hostUserId, roomId);
                return ActionResult.failure(ProblemDetail.forStatusAndDetail(
                        HttpStatus.FORBIDDEN, "Only the host can close this room."
                ));
            }

            if (!room.isOpen()) {
                logger.warn("Close failed: Room {} already closed", roomId);
                return ActionResult.failure(ProblemDetail.forStatusAndDetail(
                        HttpStatus.CONFLICT, "This room is already closed."
                ));
            }

            room.setOpen(false);
            SurveyRoom savedRoom = surveyRoomRepository.save(room);
            logger.info("Room {} closed by host {}", roomId, hostUserId);

            eventPublisher.publishEvent(new RoomClosedEvent(this, savedRoom));

            return ActionResult.success(null);

        } catch (Exception e) {
            logger.error("Failed to close room {}: {}", roomId, e.getMessage(), e);
            return ActionResult.failure(ProblemDetail.forStatusAndDetail(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "An unexpected error occurred while closing the room."
            ));
        }
    }

    @Transactional
    public void executeSystemClose(SurveyRoom room) {
        logger.info("System closing expired room: {}", room.getId());
        closeRoomInternal(room);
    }

    private ActionResult<Void> closeRoomInternal(SurveyRoom room) {
        if (!room.isOpen()) {
            return ActionResult.success(null);
        }

        room.setOpen(false);
        SurveyRoom savedRoom = surveyRoomRepository.save(room);

        eventPublisher.publishEvent(new RoomClosedEvent(this, savedRoom));

        return ActionResult.success(null);
    }
}
