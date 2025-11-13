package com.cmze.usecase;

import com.cmze.entity.SurveyForm;
import com.cmze.entity.SurveyRoom;
import com.cmze.repository.SurveyFormRepository;
import com.cmze.repository.SurveyRoomRepository;
import com.cmze.request.CreateSurveyRoomRequest;
import com.cmze.response.CreateSurveyRoomResponse;
import com.cmze.shared.ActionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.ProblemDetail;

import java.util.Optional;
import java.util.UUID;

@UseCase
public class CreateSurveyRoomUseCase {

    private static final Logger logger = LoggerFactory.getLogger(CreateSurveyRoomUseCase.class);

    private final SurveyFormRepository surveyFormRepository;
    private final SurveyRoomRepository surveyRoomRepository;

    public CreateSurveyRoomUseCase(SurveyRoomRepository surveyRoomRepository,
                                   SurveyFormRepository surveyFormRepository) {
        this.surveyRoomRepository = surveyRoomRepository;
        this.surveyFormRepository = surveyFormRepository;
    }

    @Transactional
    public ActionResult<CreateSurveyRoomResponse> execute(CreateSurveyRoomRequest request, UUID creatorUserId) {
        try {
            logger.info("Attempting to create room for survey {} by user {}", request.getSurveyFormId(), creatorUserId);

            Optional<SurveyForm> surveyFormOpt = surveyFormRepository.findById(request.getSurveyFormId());

            if (surveyFormOpt.isEmpty()) {
                logger.warn("SurveyForm not found with id: {}", request.getSurveyFormId());
                return ActionResult.failure(ProblemDetail.forStatusAndDetail(
                        HttpStatus.NOT_FOUND,
                        "SurveyForm not found with id: " + request.getSurveyFormId()
                ));
            }

            SurveyForm surveyForm = surveyFormOpt.get();

            if (surveyForm.isPrivate() && !surveyForm.getCreatorId().equals(creatorUserId)) {
                logger.warn("User {} forbidden to create room for private survey {}", creatorUserId, surveyForm.getId());
                return ActionResult.failure(ProblemDetail.forStatusAndDetail(
                        HttpStatus.FORBIDDEN,
                        "You do not have permission to create a room for this private survey."
                ));
            }

            SurveyRoom room = new SurveyRoom();
            room.setSurvey(surveyForm);
            room.setUserId(creatorUserId);
            room.setMaxParticipants(request.getMaxParticipants());

            SurveyRoom savedRoom = surveyRoomRepository.save(room);

            logger.info("Room {} created successfully for survey {}", savedRoom.getId(), surveyForm.getId());

            CreateSurveyRoomResponse response = new CreateSurveyRoomResponse(savedRoom.getId());
            return ActionResult.success(response);

        } catch (Exception e) {
            logger.error("Failed to create survey room for user {}: {}", creatorUserId, e.getMessage(), e);
            return ActionResult.failure(ProblemDetail.forStatusAndDetail(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "An unexpected error occurred while creating the room."
            ));
        }
    }
}
