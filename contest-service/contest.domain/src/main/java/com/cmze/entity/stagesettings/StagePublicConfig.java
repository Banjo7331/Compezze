package com.cmze.entity.stagesettings;

import com.cmze.entity.Stage;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "stage_public_config")
@Getter @Setter
public class StagePublicConfig {
    @Id
    @Column(name = "stage_id")
    private Long stageId;

    @OneToOne(fetch = FetchType.LAZY, optional = false) @MapsId
    @JoinColumn(name = "stage_id", nullable = false)
    private Stage stage;

    @Column(nullable = false)
    private double weight = 1.0;

    /** Nie używane logicznie, ale trzymamy spójne API; zostaw 1. */
    @Column(nullable = false)
    private int maxScore = 1;
}
