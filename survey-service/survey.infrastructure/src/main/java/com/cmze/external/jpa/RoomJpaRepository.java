package com.cmze.external.jpa;

import com.cmze.entity.RoomResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomJpaRepository extends JpaRepository<RoomResult, Long> {
}
