package com.cmze.usecase;

import com.cmze.entity.SurveyRoom;
import com.cmze.repository.SurveyEntrantRepository;
import com.cmze.repository.SurveyRoomRepository;
import com.cmze.response.GetActiveSurveyRoomResponse;
import com.cmze.shared.ActionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.transaction.annotation.Transactional;

@UseCase
public class GetAllActiveSurveyRoomsUseCase {

    private static final Logger logger = LoggerFactory.getLogger(GetAllActiveSurveyRoomsUseCase.class);
    private final SurveyRoomRepository surveyRoomRepository;
    private final SurveyEntrantRepository entrantRepository;

    public GetAllActiveSurveyRoomsUseCase( SurveyRoomRepository surveyRoomRepository,
                                           SurveyEntrantRepository entrantRepository) {
        this.surveyRoomRepository = surveyRoomRepository;
        this.entrantRepository = entrantRepository;
    }

    @Transactional(readOnly = true)
    public ActionResult<Page<GetActiveSurveyRoomResponse>> execute(Pageable pageable) {
        try {
            Page<SurveyRoom> activeRooms = surveyRoomRepository.findAllByIsOpenTrue(pageable);

            Page<GetActiveSurveyRoomResponse> responsePage = activeRooms.map(room -> {
                long participantsCount = entrantRepository.countBySurveyRoom_Id(room.getId());

                String surveyTitle = room.getSurvey().getTitle();

                return new GetActiveSurveyRoomResponse(
                        room.getId(),
                        surveyTitle,
                        room.getUserId(),
                        participantsCount,
                        room.getMaxParticipants()
                );
            });

            return ActionResult.success(responsePage);
        } catch (Exception e) {
            logger.error("Failed to fetch active rooms: {}", e.getMessage(), e);
            return ActionResult.failure(ProblemDetail.forStatusAndDetail(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Error fetching active rooms"
            ));
        }
    }
}
