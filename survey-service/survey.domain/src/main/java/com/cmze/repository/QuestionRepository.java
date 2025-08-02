package com.cmze.repository;

import com.cmze.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QuestionRepository {
    Question findById(Long id);
    Page<Question> findAllForSurvey(Long surveyId, Pageable pageable);
    Long save(Question question);
}
