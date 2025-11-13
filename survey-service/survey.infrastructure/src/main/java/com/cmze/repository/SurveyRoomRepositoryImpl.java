package com.cmze.repository;

import com.cmze.entity.SurveyRoom;
import com.cmze.external.jpa.SurveyRoomJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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
}
