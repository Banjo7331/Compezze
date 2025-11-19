package com.cmze.external.jpa;

import com.cmze.entity.SurveyRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface SurveyRoomJpaRepository extends JpaRepository<SurveyRoom, UUID> {
    @Query("SELECT sr FROM SurveyRoom sr " +
            "LEFT JOIN FETCH sr.survey s " +
            "LEFT JOIN FETCH s.questions " +
            "WHERE sr.id = :roomId")
    Optional<SurveyRoom> findByIdWithSurveyAndQuestions(@Param("roomId") UUID roomId);
}
