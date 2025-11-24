package com.cmze.usecase.form;

import com.cmze.entity.Question;
import com.cmze.entity.SurveyForm;
import com.cmze.enums.QuestionType;
import com.cmze.repository.SurveyFormRepository;
import com.cmze.request.CreateQuestionRequest;
import com.cmze.request.CreateSurveyFormRequest;
import com.cmze.response.CreateSurveyFormResponse;
import com.cmze.shared.ActionResult;
import com.cmze.usecase.UseCase;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.util.*;
import java.util.stream.Collectors;

@UseCase
public class CreateSurveyFormUseCase {

    private static final Logger logger = LoggerFactory.getLogger(CreateSurveyFormUseCase.class);
    private final SurveyFormRepository surveyFormRepository;

    @Autowired
    public CreateSurveyFormUseCase(SurveyFormRepository surveyFormRepository) {
        this.surveyFormRepository = surveyFormRepository;
    }

    @Transactional
    public ActionResult<CreateSurveyFormResponse> execute(CreateSurveyFormRequest request, UUID ownerUserId) {
        try {
            for (CreateQuestionRequest qDto : request.getQuestions()) {
                if ((qDto.getType() == QuestionType.SINGLE_CHOICE || qDto.getType() == QuestionType.MULTIPLE_CHOICE)
                        && (qDto.getPossibleChoices() == null || qDto.getPossibleChoices().isEmpty())) {

                    logger.warn("Validation failed: Choice question '{}' has no choices.", qDto.getTitle());
                    return ActionResult.failure(ProblemDetail.forStatusAndDetail(
                            HttpStatus.BAD_REQUEST,
                            "Question '" + qDto.getTitle() + "' is " + qDto.getType() + " but has no possibleChoices."
                    ));
                }
            }

            SurveyForm surveyForm = new SurveyForm();
            surveyForm.setTitle(request.getTitle());
            surveyForm.setCreatorId(ownerUserId);
            surveyForm.setPrivate(request.isPrivate());

            Set<Question> questions = request.getQuestions().stream()
                    .map(dto -> {
                        Question q = new Question();
                        q.setTitle(dto.getTitle());
                        q.setType(dto.getType());
                        q.setSurveyForm(surveyForm);

                        if (dto.getType() != QuestionType.OPEN_TEXT) {
                            q.setPossibleChoices(new HashSet<>(dto.getPossibleChoices()));
                        }
                        return q;
                    })
                    .collect(Collectors.toSet());

            surveyForm.setQuestions(questions);

            SurveyForm savedSurvey = surveyFormRepository.save(surveyForm);
            logger.info("SurveyForm created with id {} by user {}", savedSurvey.getId(), ownerUserId);

            CreateSurveyFormResponse response = new CreateSurveyFormResponse(savedSurvey.getId());
            return ActionResult.success(response);

        } catch (Exception e) {
            logger.error("Failed to create survey form for user {}: {}", ownerUserId, e.getMessage(), e);
            return ActionResult.failure(ProblemDetail.forStatusAndDetail(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "An unexpected error occurred while creating the survey."
            ));
        }
    }
}

