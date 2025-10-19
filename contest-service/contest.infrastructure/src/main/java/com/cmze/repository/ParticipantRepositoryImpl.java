package com.cmze.repository;

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
}
