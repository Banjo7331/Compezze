package com.cmze.external.jpa;

import com.cmze.entity.VoteMarker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VoteMarkerJpaRepository extends JpaRepository<VoteMarker, Long> {

    @Modifying
    @Query(value = """
    INSERT INTO vote_marker (stage_id, participant_id, submission_id, score, created_at)
    VALUES (:stageId, :participantId, :submissionId, :score, now())
    ON CONFLICT ON CONSTRAINT ux_vote_stage_participant_entry DO NOTHING
    """, nativeQuery = true)
    int tryInsert(@Param("stageId") long stageId,
                  @Param("participantId") long participantId,
                  @Param("submissionId") String submissionId,
                  @Param("score") Integer score);

    @Query(value = """
    SELECT COUNT(*) FROM vote_marker
    WHERE stage_id = :stageId AND submission_id = :submissionId AND score IS NULL
    """, nativeQuery = true)
    long countPublicFor(@Param("stageId") long stageId,
                        @Param("submissionId") String submissionId);

    @Query(value = """
    SELECT COALESCE(SUM(score),0) FROM vote_marker
    WHERE stage_id = :stageId AND submission_id = :submissionId AND score IS NOT NULL
    """, nativeQuery = true)
    long sumJuryFor(@Param("stageId") long stageId,
                    @Param("submissionId") String submissionId);
}
