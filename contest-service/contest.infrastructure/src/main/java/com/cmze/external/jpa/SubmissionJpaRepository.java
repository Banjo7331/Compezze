package com.cmze.external.jpa;

import com.cmze.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmissionJpaRepository extends JpaRepository<Submission, String> {
    boolean existsByContest_IdAndParticipantId(String contestId, String participantId);
    long countByContest_Id(String contestId);
}