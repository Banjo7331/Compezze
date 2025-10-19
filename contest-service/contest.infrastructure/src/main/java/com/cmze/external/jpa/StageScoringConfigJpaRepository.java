package com.cmze.external.jpa;

import com.cmze.entity.stagesettings.StagePublicConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StageScoringConfigJpaRepository extends JpaRepository<StagePublicConfig, Long> {}
