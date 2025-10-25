package com.cmze.repository;

import com.cmze.entity.Participant;
import com.cmze.external.jpa.ParticipantJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ParticipantRepositoryImpl implements ParticipantRepository{

    private final ParticipantJpaRepository impl;

    @Autowired
    public ParticipantRepositoryImpl(ParticipantJpaRepository impl) {
        this.impl = impl;
    }

    @Override
    public boolean existsByContestIdAndUserId(String contestId, String userId) {
        return false;
    }

    @Override
    public Participant findsByContestIdAndUserId(String contestId, String userId) {
        return null;
    }

}
