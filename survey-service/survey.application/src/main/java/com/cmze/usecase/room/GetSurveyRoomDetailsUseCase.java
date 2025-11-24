package com.cmze.usecase.room;

import com.cmze.entity.SurveyRoom;
import com.cmze.repository.SurveyRoomRepository;
import com.cmze.response.GetSurveyRoomDetailsResponse;
import com.cmze.shared.ActionResult;
import com.cmze.spi.helpers.room.FinalRoomResultDto;
import com.cmze.spi.helpers.room.SurveyResultCounter;
import com.cmze.usecase.UseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@UseCase
public class GetSurveyRoomDetailsUseCase {

    private final SurveyRoomRepository surveyRoomRepository;
    private final SurveyResultCounter surveyResultCounter;

    public GetSurveyRoomDetailsUseCase(SurveyRoomRepository surveyRoomRepository,
                                       SurveyResultCounter surveyResultCounter) {
        this.surveyRoomRepository = surveyRoomRepository;
        this.surveyResultCounter = surveyResultCounter;
    }

    @Transactional(readOnly = true)
    public ActionResult<GetSurveyRoomDetailsResponse> execute(UUID roomId) {
        SurveyRoom room = surveyRoomRepository.findByIdWithSurveyAndQuestions(roomId).orElse(null);
        if (room == null) {
            return ActionResult.failure(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "Room not found"));
        }

        FinalRoomResultDto results = surveyResultCounter.calculate(room.getId());

        GetSurveyRoomDetailsResponse response = new GetSurveyRoomDetailsResponse(
                room.getId(),
                room.getSurvey().getTitle(),
                room.getUserId(),
                room.isOpen(),
                room.isPrivate(),
                results.getTotalParticipants(),
                results
        );

        return ActionResult.success(response);
    }
}
