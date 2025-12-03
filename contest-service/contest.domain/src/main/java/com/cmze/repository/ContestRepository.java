package com.cmze.repository;

import com.cmze.entity.Contest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ContestRepository {
    Optional<Contest> findById(Long id);

    Page<Contest> findAll(Pageable pageable, Long quizId);

    Contest save(Contest contest);

    List<Contest> findUpcomingForUser(String userId, Pageable pageable);
}
