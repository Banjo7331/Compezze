package com.cmze.external.jpa;

import com.cmze.entity.SurveyRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SurveyRoomJpaRepository extends JpaRepository<SurveyRoom, UUID> {
}
