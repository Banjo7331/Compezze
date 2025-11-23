package com.cmze.internal.ws;

import com.cmze.entity.*;
import com.cmze.enums.QuestionType;
import com.cmze.repository.SurveyEntrantRepository;
import com.cmze.repository.SurveyRoomRepository;
import com.cmze.spi.helpers.room.FinalRoomResultDto;
import com.cmze.spi.helpers.room.QuestionResultDto;
import com.cmze.spi.helpers.room.SurveyResultCounter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class SurveyResultCounterImpl implements SurveyResultCounter {

    private final SurveyRoomRepository surveyRoomRepository;
    private final SurveyEntrantRepository surveyEntrantRepository;

    public SurveyResultCounterImpl(SurveyRoomRepository surveyRoomRepository,
                                   SurveyEntrantRepository surveyEntrantRepository) {
        this.surveyRoomRepository = surveyRoomRepository;
        this.surveyEntrantRepository = surveyEntrantRepository;
    }

    public FinalRoomResultDto calculate(UUID roomId) {

        SurveyRoom room = surveyRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found during calculation"));

        List<SurveyEntrant> participants = surveyEntrantRepository.findAllBySurveyRoomId(roomId);

        List<SurveyAttempt> submissions = participants.stream()
                .map(SurveyEntrant::getSurveyAttempt)
                .filter(java.util.Objects::nonNull)
                .toList();

        List<QuestionResultDto> questionResults = new ArrayList<>();

        for (Question question : room.getSurvey().getQuestions()) {
            QuestionResultDto qr = new QuestionResultDto();
            qr.setQuestionId(question.getId());
            qr.setTitle(question.getTitle());
            qr.setType(question.getType());

            var allAnswersToThisQuestion = submissions.stream()
                    .flatMap(attempt -> attempt.getParticipantAnswers().stream())
                    .filter(answer -> answer.getQuestion().getId().equals(question.getId()))
                    .toList();

            if (question.getType() == QuestionType.OPEN_TEXT) {
                List<String> open = allAnswersToThisQuestion.stream()
                        .flatMap(pa -> pa.getAnswer().stream())
                        .collect(Collectors.toList());
                qr.setOpenAnswers(open);
                qr.setAnswerCounts(new HashMap<>());
            } else {
                Map<String, Long> counts = allAnswersToThisQuestion.stream()
                        .flatMap(pa -> pa.getAnswer().stream())
                        .collect(Collectors.groupingBy(
                                Function.identity(),
                                Collectors.counting()
                        ));
                qr.setAnswerCounts(counts);
                qr.setOpenAnswers(new ArrayList<>());
            }
            questionResults.add(qr);
        }

        return new FinalRoomResultDto(
                (long) participants.size(),
                (long) submissions.size(),
                questionResults
        );
    }
}
