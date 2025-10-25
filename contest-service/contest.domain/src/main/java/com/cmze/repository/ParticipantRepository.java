package com.cmze.repository;

import com.cmze.entity.Contest;
import com.cmze.entity.Participant;

public interface ParticipantRepository {
    Participant findsByContestIdAndUserId(String contestId, String userId);
    Participant save(Participant participant);
}
