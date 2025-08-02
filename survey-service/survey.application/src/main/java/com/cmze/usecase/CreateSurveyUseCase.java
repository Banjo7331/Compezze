package com.cmze.usecase;

import com.cmze.entity.Question;
import com.cmze.entity.SurveyForm;
import com.cmze.repository.SurveyFormRepository;
import com.cmze.request.CreateSurveyRequest;
import com.cmze.response.CreateSurveyResponse;
import com.cmze.shared.ActionResult;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.util.List;
import java.util.stream.Collectors;

@UseCase
public class CreateSurveyUseCase {

    public final SurveyFormRepository surveyFormRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public CreateSurveyUseCase(SurveyFormRepository surveyFormRepository, ModelMapper modelMapper) {
        this.surveyFormRepository = surveyFormRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public ActionResult<CreateSurveyResponse> execute(CreateSurveyRequest createSurveyRequest) {
        try {
            SurveyForm survey = modelMapper.map(createSurveyRequest, SurveyForm.class);

            if (createSurveyRequest.getCreateQuestionRequests() != null && !createSurveyRequest.getCreateQuestionRequests().isEmpty()) {
                List<Question> questions = createSurveyRequest.getCreateQuestionRequests().stream()
                        .map(questionRequest -> {
                            Question question = modelMapper.map(questionRequest, Question.class);
                            question.setSurveyForm(survey);
                            return question;
                        })
                        .collect(Collectors.toList());

                survey.setQuestions(questions);
            }

            Long surveyId = surveyFormRepository.save(survey);
            return ActionResult.success(new CreateSurveyResponse(surveyId));

        } catch (Exception ex) {
            ProblemDetail error = ProblemDetail.forStatusAndDetail(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to create survey: " + ex.getMessage()
            );
            return ActionResult.failure(error);
        }
    }
}

