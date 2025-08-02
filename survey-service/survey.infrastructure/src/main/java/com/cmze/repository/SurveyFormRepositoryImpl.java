package com.cmze.repository;

import com.cmze.entity.Question;
import com.cmze.entity.SurveyForm;
import com.cmze.external.jpa.SurveyFormJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class SurveyFormRepositoryImpl implements SurveyFormRepository {

    private final SurveyFormJpaRepository impl;

    @Autowired
    public SurveyFormRepositoryImpl(SurveyFormJpaRepository impl) {
        this.impl = impl;
    }

    @Override
    public SurveyForm findById(Long id) {
        return impl.findById(id).orElseThrow(()-> new RuntimeException("SurveyForm not found"));
    }

    @Override
    public Page<SurveyForm> findAll(Pageable pageable) {
        return impl.findAll(pageable);
    }

    @Override
    public Long save(SurveyForm survey) {
        return impl.save(survey).getId();
    }
}
