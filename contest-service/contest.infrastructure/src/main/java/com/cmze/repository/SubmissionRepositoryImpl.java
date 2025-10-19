package com.cmze.repository;

import com.cmze.entity.Submission;
import com.cmze.enums.SubmissionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class SubmissionRepositoryImpl implements SubmissionRepository {
    @Override
    public Page<Submission> findByContest_IdAndStatus(String contestId, SubmissionStatus status, Pageable pageable) {
        return null;
    }

    @Override
    public Optional<Submission> findByIdAndContest_Id(String submissionId, String contestId) {
        return Optional.empty();
    }
}
