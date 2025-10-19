package com.cmze.external.jpa;

import com.cmze.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantJpaRepository  extends JpaRepository<Participant, Long> {
}
