package com.cmze.external.jpa;

import com.cmze.entity.SurveyForm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyFormJpaRepository extends JpaRepository<SurveyForm, Long> {
}
