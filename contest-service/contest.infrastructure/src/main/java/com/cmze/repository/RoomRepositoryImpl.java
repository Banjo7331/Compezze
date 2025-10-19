package com.cmze.repository;

import com.cmze.entity.Room;
import org.springframework.stereotype.Repository;

@Repository
public class RoomRepositoryImpl implements RoomRepository {
    @Override
    public Room findByContestId(String contestId) {
        return null;
    }

    @Override
    public boolean existsByRoomKey(String roomKey) {
        return false;
    }

    @Override
    public Room findByRoomKey(String roomKey) {
        return null;
    }
}
