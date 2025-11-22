package com.cmze.repository;

import com.cmze.entity.SurveyRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface SurveyRoomRepository {
    SurveyRoom save(SurveyRoom surveyRoom);
    Optional<SurveyRoom> findById(UUID roomId);
    Optional<SurveyRoom> findByIdWithSurveyAndQuestions(UUID roomId);
    Page<SurveyRoom> findAllByIsOpenTrue(Pageable pageable);
}
