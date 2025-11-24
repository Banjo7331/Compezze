package com.cmze.usecase.room;

import com.cmze.entity.SurveyRoom;
import com.cmze.repository.SurveyRoomRepository;
import com.cmze.response.MySurveyRoomResultsResponse;
import com.cmze.shared.ActionResult;
import com.cmze.spi.helpers.room.FinalRoomResultDto;
import com.cmze.spi.helpers.room.SurveyResultCounter;
import com.cmze.usecase.UseCase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@UseCase
public class GetMySurveyRoomsResultsUseCase {

    private final SurveyRoomRepository surveyRoomRepository;
    private final SurveyResultCounter surveyResultCounter;

    public GetMySurveyRoomsResultsUseCase(SurveyRoomRepository surveyRoomRepository,
                                          SurveyResultCounter surveyResultCounter) {
        this.surveyRoomRepository = surveyRoomRepository;
        this.surveyResultCounter = surveyResultCounter;
    }

    @Transactional(readOnly = true)
    public ActionResult<Page<MySurveyRoomResultsResponse>> execute(UUID userId, Pageable pageable) {
        Page<SurveyRoom> roomsPage = surveyRoomRepository.findByUserId(userId, pageable);

        Page<MySurveyRoomResultsResponse> dtoPage = roomsPage.map(room -> {
            FinalRoomResultDto stats = surveyResultCounter.calculate(room.getId());

            return new MySurveyRoomResultsResponse(
                    room.getId(),
                    room.getSurvey().getTitle(),
                    room.isOpen(),
                    room.isPrivate(),
                    room.getCreatedAt(),
                    room.getValidUntil(),
                    stats.getTotalParticipants(),
                    stats.getTotalSubmissions()
            );
        });

        return ActionResult.success(dtoPage);
    }
}
