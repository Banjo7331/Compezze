package com.cmze.external.jpa;

import com.cmze.entity.Contest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContestJpaRepository extends JpaRepository<Contest, Long> {

}
