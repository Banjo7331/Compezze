package com.cmze.usecase;

import com.cmze.entity.ParticipantAnswer;
import com.cmze.entity.Question;
import com.cmze.entity.SurveyAttempt;
import com.cmze.entity.SurveyEntrant;
import com.cmze.repository.SurveyAttemptRepository;
import com.cmze.repository.SurveyEntrantRepository;
import com.cmze.request.SubmitSurveyAttemptRequest.SubmitParticipantAnswerRequest;
import com.cmze.request.SubmitSurveyAttemptRequest.SubmitSurveyAttemptRequest;
import com.cmze.shared.ActionResult;
import com.cmze.ws.event.SurveyAttemptSubmittedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@UseCase
public class SubmitSurveyAttemptUseCase {

    private static final Logger logger = LoggerFactory.getLogger(SubmitSurveyAttemptUseCase.class);
    private final SurveyEntrantRepository surveyEntrantRepository;
    private final SurveyAttemptRepository surveyAttemptRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public SubmitSurveyAttemptUseCase(
            SurveyEntrantRepository surveyEntrantRepository,
            SurveyAttemptRepository surveyAttemptRepository,
            ApplicationEventPublisher eventPublisher
    ) {
        this.surveyEntrantRepository = surveyEntrantRepository;
        this.surveyAttemptRepository = surveyAttemptRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public ActionResult<?> execute(UUID roomId, UUID participantUserId, SubmitSurveyAttemptRequest request) {
        try {
            Optional<SurveyEntrant> participantOpt = surveyEntrantRepository
                    .findBySurveyRoom_IdAndParticipantUserId(roomId, participantUserId);

            if (participantOpt.isEmpty()) {
                logger.warn("Submit failed: User {} has not joined room {}", participantUserId, roomId);
                return ActionResult.failure(ProblemDetail.forStatusAndDetail(
                        HttpStatus.FORBIDDEN, "You have not joined this room."
                ));
            }
            SurveyEntrant participant = participantOpt.get();

            if (!participant.getSurveyRoom().isOpen()) {
                logger.warn("Submit failed: Room {} is closed", roomId);
                return ActionResult.failure(ProblemDetail.forStatusAndDetail(
                        HttpStatus.GONE, "This room has been closed by the host."
                ));
            }

            if (participant.getSurveyAttempt() != null) {
                logger.warn("Submit failed: User {} already submitted in room {}", participantUserId, roomId);
                return ActionResult.failure(ProblemDetail.forStatusAndDetail(
                        HttpStatus.CONFLICT, "You have already submitted your answers for this room."
                ));
            }

            Map<Long, Question> validQuestions = participant.getSurveyRoom().getSurvey().getQuestions().stream()
                    .collect(Collectors.toMap(Question::getId, Function.identity()));

            for (SubmitParticipantAnswerRequest answerDto : request.getParticipantAnswers()) {
                if (!validQuestions.containsKey(answerDto.getQuestionId())) {
                    logger.warn("Submit failed: Invalid questionId {} submitted by user {}", answerDto.getQuestionId(), participantUserId);
                    return ActionResult.failure(ProblemDetail.forStatusAndDetail(
                            HttpStatus.BAD_REQUEST, "Invalid questionId provided: " + answerDto.getQuestionId()
                    ));
                }
            }

            SurveyAttempt surveyAttempt = new SurveyAttempt();
            surveyAttempt.setParticipant(participant);
            surveyAttempt.setSurvey(participant.getSurveyRoom().getSurvey());

            List<ParticipantAnswer> answers = request.getParticipantAnswers().stream()
                    .map(dto -> {
                        ParticipantAnswer pa = new ParticipantAnswer();
                        pa.setQuestion(validQuestions.get(dto.getQuestionId()));
                        pa.setAnswer(dto.getAnswers());
                        pa.setSurveyAttempt(surveyAttempt); // Link zwrotny
                        return pa;
                    })
                    .collect(Collectors.toList());

            surveyAttempt.setParticipantAnswers(answers);

            SurveyAttempt savedAttempt = surveyAttemptRepository.save(surveyAttempt);
            logger.info("User {} successfully submitted answers for room {}", participantUserId, roomId);

            eventPublisher.publishEvent(new SurveyAttemptSubmittedEvent(this, savedAttempt));

            return ActionResult.success(null);

        } catch (Exception e) {
            logger.error("Failed to submit survey for user {}: {}", participantUserId, e.getMessage(), e);
            return ActionResult.failure(ProblemDetail.forStatusAndDetail(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "An unexpected error occurred while submitting answers."
            ));
        }
    }
}
