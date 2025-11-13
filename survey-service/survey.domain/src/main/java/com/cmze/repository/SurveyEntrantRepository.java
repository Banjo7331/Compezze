package com.cmze.repository;

import com.cmze.entity.SurveyEntrant;

import java.util.Optional;
import java.util.UUID;

public interface SurveyEntrantRepository {
    Optional<SurveyEntrant> findById(Long Id);
    SurveyEntrant save(SurveyEntrant entrant);
    boolean existsBySurveyRoom_IdAndParticipantUserId(UUID roomId, UUID participantUserId);
}
