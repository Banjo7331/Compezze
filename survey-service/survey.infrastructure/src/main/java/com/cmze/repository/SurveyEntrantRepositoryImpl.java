package com.cmze.repository;

import com.cmze.entity.SurveyEntrant;
import com.cmze.external.jpa.SurveyEntrantJpaRepository;
import com.cmze.external.jpa.SurveyFormJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class SurveyEntrantRepositoryImpl implements SurveyEntrantRepository{

    private final SurveyEntrantJpaRepository impl;

    @Autowired
    public SurveyEntrantRepositoryImpl(SurveyEntrantJpaRepository impl) {
        this.impl = impl;
    }

    @Override
    public Optional<SurveyEntrant> findById(Long Id) {
        return Optional.empty();
    }

    @Override
    public Optional<SurveyEntrant> findBySurveyRoom_IdAndParticipantUserId(UUID roomId, UUID entrantId) {
        return Optional.empty();
    }

    @Override
    public SurveyEntrant save(SurveyEntrant entrant) {
        return null;
    }

    @Override
    public boolean existsBySurveyRoom_IdAndParticipantUserId(UUID roomId, UUID participantUserId) {
        return false;
    }

    @Override
    public Long countBySurveyRoom_Id(UUID roomId) {
        return 0;
    }
}
