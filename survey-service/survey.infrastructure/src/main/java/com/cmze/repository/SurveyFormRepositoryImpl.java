package com.cmze.repository;

import com.cmze.entity.Question;
import com.cmze.entity.SurveyForm;
import com.cmze.external.jpa.SurveyFormJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class SurveyFormRepositoryImpl implements SurveyFormRepository {

    private final SurveyFormJpaRepository impl;

    @Autowired
    public SurveyFormRepositoryImpl(SurveyFormJpaRepository impl) {
        this.impl = impl;
    }

    @Override
    public Optional<SurveyForm> findById(Long id) {
        return impl.findById(id);
    }

    @Override
    public Page<SurveyForm> findAll(Pageable pageable) {
        return impl.findAll(pageable);
    }

    @Override
    public Page<SurveyForm> findAllPublicAndOwnedByUser(UUID currentUserId, Pageable pageable) {
        return impl.findAllPublicAndOwnedByUser(currentUserId, pageable);
    }

    @Override
    public SurveyForm save(SurveyForm survey) {
        return impl.save(survey);
    }
}
