package com.cmze.repository;

import com.cmze.entity.Submission;
import com.cmze.enums.SubmissionStatus;

import java.util.List;
import java.util.Optional;

public interface SubmissionRepository{
    Optional<Submission> findByIdAndContest_Id(String submissionId, Long contestId);

    List<Submission> findAllByContest_IdAndStatus(Long contestId, SubmissionStatus status);

    Submission save(Submission submission);
}
