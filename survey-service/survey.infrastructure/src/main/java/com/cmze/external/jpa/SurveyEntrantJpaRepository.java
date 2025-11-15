package com.cmze.external.jpa;

import com.cmze.entity.SurveyEntrant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyEntrantJpaRepository extends JpaRepository<SurveyEntrant, Long> {
}
