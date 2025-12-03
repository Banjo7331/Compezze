package com.cmze.repository;

import com.cmze.entity.Submission;
import com.cmze.enums.SubmissionStatus;
import com.cmze.external.jpa.SubmissionJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class SubmissionRepositoryImpl implements SubmissionRepository {

    private final SubmissionJpaRepository impl;

    public SubmissionRepositoryImpl(SubmissionJpaRepository impl) {
        this.impl = impl;
    }

    @Override
    public Optional<Submission> findByIdAndContest_Id(String submissionId, Long contestId) {
        return Optional.empty();
    }

    @Override
    public List<Submission> findAllByContest_IdAndStatus(Long contestId, SubmissionStatus status) {
        return impl.findAllByContest_IdAndStatus(contestId, status);
    }

    @Override
    public Submission save(Submission submission) {
        return impl.save(submission);
    }
}
