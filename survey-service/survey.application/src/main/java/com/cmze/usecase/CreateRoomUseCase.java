package com.cmze.usecase;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

@UseCase
public class CreateRoomUseCase {

    private final SurveyRoomRepository surveyRoomRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public CreateRoomUseCase(SurveyRoomRepository surveyRoomRepository, ModelMapper modelMapper) {
        this.surveyRoomRepository = surveyRoomRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public ActionResult<CreateSurveyRoomResponse> execute(CreateSurveyRoomRequest request) {
        try {
            if (!StringUtils.hasText(request.getSurveyId()) || !StringUtils.hasText(request.getCreatorId())) {
                ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                        HttpStatus.BAD_REQUEST, "SurveyId and CreatorId are required."
                );
                return ActionResult.failure(problem);
            }

            // Tworzenie pokoju
            SurveyRoom surveyRoom = new SurveyRoom();
            surveyRoom.setId(UUID.randomUUID().toString());
            surveyRoom.setSurveyId(request.getSurveyId());
            surveyRoom.setCreatorId(request.getCreatorId());
            surveyRoom.setMaxUsers(request.getMaxUsers());
            surveyRoom.setLink("https://localhost:5731/survey-room/" + request.getSurveyId() + "/" + surveyRoom.getId());

            // Zapis do bazy
            surveyRoomRepository.save(surveyRoom);

            CreateSurveyRoomResponse response = new CreateSurveyRoomResponse(
                    surveyRoom.getId(),
                    surveyRoom.getLink()
            );
            return ActionResult.success(response);

        } catch (Exception ex) {
            ProblemDetail error = ProblemDetail.forStatusAndDetail(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to create survey room: " + ex.getMessage()
            );
            return ActionResult.failure(error);
        }
    }
}
