package com.cmze.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "vote_marker",
        uniqueConstraints = {
                // Wariant A: 1 głos NA ETAP (niezależnie od entry) – odblokuj jeśli chcesz taką regułę:
                // @UniqueConstraint(name = "ux_vote_stage_participant", columnNames = {"stage_id","participant_id"}),

                // Wariant B: 1 głos NA ENTRY w ETAPIE (najczęstsze) – zwykle to chcemy:
                @UniqueConstraint(name = "ux_vote_stage_participant_entry", columnNames = {"stage_id","participant_id","submission_id"})
        },
        indexes = {
                @Index(name="ix_vote_stage_entry", columnList="stage_id,submission_id"),
                @Index(name="ix_vote_stage_participant", columnList="stage_id,participant_id")
        }
)
@Getter
@Setter
public class VoteMarker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "stage_id", nullable = false)
    private Stage stage;

    /** Kto głosuje (uczestnik/uczestnik-publiczności powiązany z konkursem). */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "participant_id", nullable = false)
    private Participant participant;

    /** Na co głosuje. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "submission_id", nullable = false)
    private Submission submission;

    /** Dla jury: punktacja; dla public: null (albo 1 – wtedy możesz mieć jedną ścieżkę liczenia). */
    @Column(name = "score")
    private Integer score; // null dla public; 0..max dla jury

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();
}
