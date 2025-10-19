package com.cmze.external.jpa;

import com.cmze.entity.Stage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StageJpaRepository extends JpaRepository<Stage, Long> {
    List<Stage> findAllByContest_IdOrderByPositionAsc(String contestId);

    Optional<Stage> findByContest_IdAndPosition(String contestId, int position);

    Optional<Stage> findFirstByContest_IdOrderByPositionAsc(String contestId);

    Optional<Stage> findFirstByContest_IdAndPositionGreaterThanOrderByPositionAsc(String contestId, int position);
}
