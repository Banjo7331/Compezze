package com.cmze.repository;

import com.cmze.entity.SurveyRoom;
import com.cmze.external.jpa.SurveyRoomJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class SurveyRoomRepositoryImpl implements SurveyRoomRepository{

    private final SurveyRoomJpaRepository impl;

    @Autowired
    public SurveyRoomRepositoryImpl(SurveyRoomJpaRepository impl) {
        this.impl = impl;
    }

    @Override
    public SurveyRoom save(SurveyRoom surveyRoom) {
        return impl.save(surveyRoom);
    }

    @Override
    public Optional<SurveyRoom> findById(UUID roomId) {
        return impl.findById(roomId);
    }
}
