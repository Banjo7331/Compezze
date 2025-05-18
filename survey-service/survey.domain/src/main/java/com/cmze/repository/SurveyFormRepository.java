package com.cmze.repository;

import com.cmze.entity.Question;
import com.cmze.entity.SurveyForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SurveyFormRepository {
    SurveyForm findById(Long Id);
    Page<SurveyForm> findAll(Pageable pageable);
    Long save(SurveyForm surveyForm);
}
