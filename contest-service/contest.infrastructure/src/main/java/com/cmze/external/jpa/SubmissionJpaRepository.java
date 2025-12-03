package com.cmze.external.jpa;

import com.cmze.entity.Submission;
import com.cmze.enums.SubmissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubmissionJpaRepository extends JpaRepository<Submission, String> {
    boolean existsByContest_IdAndParticipantId(Long contestId, Long participantId);
    long countByContest_Id(Long contestId);
    List<Submission> findAllByContest_IdAndStatus(Long contestId, SubmissionStatus status);
}