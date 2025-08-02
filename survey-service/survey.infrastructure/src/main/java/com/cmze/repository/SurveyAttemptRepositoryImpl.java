package com.cmze.repository;

import com.cmze.entity.SurveyAttempt;
import com.cmze.external.jpa.SurveyAttemptJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class SurveyAttemptRepositoryImpl implements SurveyAttemptRepository {

    private final SurveyAttemptJpaRepository impl;

    @Autowired
    public SurveyAttemptRepositoryImpl(SurveyAttemptJpaRepository impl) {
        this.impl = impl;
    }

    @Override
    public SurveyAttempt findById(Long id) {
        return impl.findById(id).orElseThrow(()-> new RuntimeException("SurveyAttempt not found"));
    }

    @Override
    public Page<SurveyAttempt> findAll(Pageable pageable) {
        return impl.findAll(pageable);
    }

    @Override
    public Long save(SurveyAttempt attempt) {
        return impl.save(attempt).getId();
    }
}
