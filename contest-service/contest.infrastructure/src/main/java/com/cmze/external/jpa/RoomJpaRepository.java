package com.cmze.external.jpa;

import com.cmze.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomJpaRepository extends JpaRepository<Room, String> {
    Optional<Room> findByContest_Id(String contestId);
    Optional<Room> findByRoomKey(String roomKey);
    boolean existsByRoomKey(String roomKey);
}
