package com.cmze.external.jpa;

import com.cmze.entity.SurveyEntrant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SurveyEntrantJpaRepository extends JpaRepository<SurveyEntrant, Long> {
    boolean existsBySurveyRoom_IdAndUserId(UUID roomId, UUID participantUserId);

    long countBySurveyRoom_Id(UUID roomId);

    Optional<SurveyEntrant> findBySurveyRoom_IdAndUserId(UUID roomId, UUID participantUserId);
}
