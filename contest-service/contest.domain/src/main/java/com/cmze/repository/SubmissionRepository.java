package com.cmze.repository;

import com.cmze.entity.Submission;
import com.cmze.enums.SubmissionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface SubmissionRepository{
    Page<Submission> findByContest_IdAndStatus(String contestId, SubmissionStatus status, Pageable pageable);

    Optional<Submission> findByIdAndContest_Id(String submissionId, String contestId);
}
