package com.cmze.repository;

import com.cmze.entity.SurveyRoom;

import java.util.Optional;
import java.util.UUID;

public interface SurveyRoomRepository {
    SurveyRoom save(SurveyRoom surveyRoom);
    Optional<SurveyRoom> findById(UUID roomId);
    Optional<SurveyRoom> findByIdWithSurveyAndQuestions(UUID roomId);
}
