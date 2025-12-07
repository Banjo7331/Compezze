package com.cmze.repository;

import com.cmze.entity.Room;

import java.util.Optional;

public interface RoomRepository {
    boolean existsByRoomKey(String roomKey);
    Optional<Room> findByRoomKey(String roomKey);
    Optional<Room> findByContest_Id(Long contestId);
    Room save(Room room);
    boolean existsByContest_IdAndActiveTrue(Long contestId);
}
