package com.cmze.repository;

import com.cmze.entity.SurveyAttempt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SurveyAttemptRepository {
    SurveyAttempt findById(Long Id);
    Page<SurveyAttempt> findAll(Pageable pageable);
    Long save(SurveyAttempt surveyAttempt);
}
