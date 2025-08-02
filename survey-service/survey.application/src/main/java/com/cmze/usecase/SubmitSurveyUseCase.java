package com.cmze.usecase;

import com.cmze.entity.ParticipantAnswer;
import com.cmze.entity.Question;
import com.cmze.entity.SurveyAttempt;
import com.cmze.entity.SurveyForm;
import com.cmze.repository.QuestionRepository;
import com.cmze.repository.SurveyAttemptRepository;
import com.cmze.repository.SurveyFormRepository;
import com.cmze.request.SubmitParticipantAnswerRequest;
import com.cmze.request.SubmitSurveyRequest;
import com.cmze.response.CreateSurveyResponse;
import com.cmze.response.SubmitSurveyResponse;
import com.cmze.shared.ActionResult;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@UseCase
public class SubmitSurveyUseCase {

    private final SurveyFormRepository surveyFormRepository;
    private final SurveyAttemptRepository surveyAttemptRepository;
    private final QuestionRepository questionRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public SubmitSurveyUseCase(
            SurveyFormRepository surveyFormRepository,
            SurveyAttemptRepository surveyAttemptRepository,
            QuestionRepository questionRepository,
            ModelMapper modelMapper
    ) {
        this.surveyFormRepository = surveyFormRepository;
        this.surveyAttemptRepository = surveyAttemptRepository;
        this.questionRepository = questionRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public ActionResult<SubmitSurveyResponse> execute(Long surveyId, SubmitSurveyRequest submitSurveyRequest) {
        SurveyForm survey = surveyFormRepository.findById(surveyId);
        if (survey == null) {
            ProblemDetail notFound = ProblemDetail.forStatusAndDetail(
                    HttpStatus.NOT_FOUND, "Survey not found with ID: " + surveyId
            );
            return ActionResult.failure(notFound);
        }

        SurveyAttempt surveyAttempt = new SurveyAttempt();
        surveyAttempt.setSurvey(survey);

        List<ParticipantAnswer> participantAnswers = new ArrayList<>();

        for (SubmitParticipantAnswerRequest submitParticipantAnswer : submitSurveyRequest.getParticipantAnswers()) {
            Question question = survey.getQuestions().stream()
                    .filter(q -> Objects.equals(q.getId(), submitParticipantAnswer.getQuestionId()))
                    .findFirst()
                    .orElse(null);

            if (question == null) {
                ProblemDetail invalid = ProblemDetail.forStatusAndDetail(
                        HttpStatus.BAD_REQUEST,
                        "Invalid question ID: " + submitParticipantAnswer.getQuestionId()
                );
                return ActionResult.failure(invalid);
            }

            ParticipantAnswer participantAnswer = modelMapper.map(submitParticipantAnswer, ParticipantAnswer.class);
            participantAnswer.setQuestion(question);
            participantAnswer.setSurveyAttempt(surveyAttempt);
            participantAnswers.add(participantAnswer);
        }

        surveyAttempt.setParticipantAnswers(participantAnswers);

        Long attemptId = surveyAttemptRepository.save(surveyAttempt);

        return ActionResult.success(new SubmitSurveyResponse(attemptId));
    }
}
