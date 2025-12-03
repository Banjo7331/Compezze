package com.cmze.repository;

import com.cmze.entity.Participant;

import java.util.Optional;

public interface ParticipantRepository {
    Participant save(Participant participant);

    Optional<Participant> findByContestIdAndUserId(Long contestId, String userId);

    long countByContest_Id(Long contestId);
}
