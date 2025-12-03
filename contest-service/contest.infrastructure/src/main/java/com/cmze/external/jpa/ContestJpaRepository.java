package com.cmze.external.jpa;

import com.cmze.entity.Contest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContestJpaRepository extends JpaRepository<Contest, Long> {

    @Query("SELECT c FROM Contest c " +
            "LEFT JOIN c.participants p " +
            "WHERE (c.organizerId = :userId OR p.userId = :userId) " +
            "AND c.status != 'FINISHED' " +
            "ORDER BY c.startDate ASC")
    List<Contest> findUpcomingForUser(@Param("userId") String userId, Pageable pageable);

}
