package com.cmze.usecase;

import com.cmze.entity.Question;
import com.cmze.entity.SurveyForm;
import com.cmze.repository.SurveyFormRepository;
import com.cmze.request.EditQuestionRequest;
import com.cmze.request.EditSurveyRequest;
import com.cmze.response.EditSurveyResponse;
import com.cmze.shared.ActionResult;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@UseCase
public class EditSurveyUseCase {
    public final SurveyFormRepository surveyFormRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public EditSurveyUseCase(SurveyFormRepository surveyFormRepository, ModelMapper modelMapper) {
        this.surveyFormRepository = surveyFormRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public ActionResult<EditSurveyResponse> execute(EditSurveyRequest editSurveyRequest) {
        SurveyForm survey = surveyFormRepository.findById(editSurveyRequest.getId());

        if (survey == null) {
            ProblemDetail notFound = ProblemDetail.forStatusAndDetail(
                    HttpStatus.NOT_FOUND,
                    "Survey not found with ID: " + editSurveyRequest.getId()
            );
            return ActionResult.failure(notFound);
        }

        try {
            modelMapper.map(editSurveyRequest, survey);

            if (editSurveyRequest.getEditQuestionRequests() != null) {
                Map<Long, Question> existingQuestionsMap = survey.getQuestions().stream()
                        .collect(Collectors.toMap(Question::getId, Function.identity()));

                List<Question> updatedQuestions = new ArrayList<>();

                for (EditQuestionRequest question : editSurveyRequest.getEditQuestionRequests()) {
                    if (question.getId() == null) {
                        Question newQuestion = modelMapper.map(question, Question.class);
                        newQuestion.setSurveyForm(survey);
                        updatedQuestions.add(newQuestion);
                    } else {
                        Question existing = existingQuestionsMap.remove(question.getId());
                        if (existing != null) {
                            modelMapper.map(question, existing);
                            updatedQuestions.add(existing);
                        } else {
                            ProblemDetail questionNotFound = ProblemDetail.forStatusAndDetail(
                                    HttpStatus.BAD_REQUEST,
                                    "Question ID not found: " + question.getId()
                            );
                            return ActionResult.failure(questionNotFound);
                        }
                    }
                }

                for (Question toDelete : existingQuestionsMap.values()) {
                    survey.getQuestions().remove(toDelete);
                }

                survey.setQuestions(updatedQuestions);
            }

            surveyFormRepository.save(survey);
            return ActionResult.success(new EditSurveyResponse(survey.getId()));

        } catch (Exception ex) {
            ProblemDetail error = ProblemDetail.forStatusAndDetail(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unexpected error while editing survey: " + ex.getMessage()
            );
            return ActionResult.failure(error);
        }
    }
}

