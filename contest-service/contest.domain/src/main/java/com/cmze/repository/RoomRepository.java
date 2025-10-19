package com.cmze.repository;

import com.cmze.entity.Room;

public interface RoomRepository {
    Room findByContestId(String contestId);
    boolean existsByRoomKey(String roomKey);
    Room findByRoomKey(String roomKey);
}
