package com.cmze.repository;

import com.cmze.entity.Contest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ContestRepository {
    Contest findById(String id);

    Page<Contest> findAll(Pageable pageable, Long quizId);

    Contest save(Contest contest);
}
