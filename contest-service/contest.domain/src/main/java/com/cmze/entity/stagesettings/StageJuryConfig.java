package com.cmze.entity.stagesettings;

import com.cmze.entity.Stage;
import com.cmze.enums.JuryRevealMode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "stage_jury_config")
@Getter @Setter
public class StageJuryConfig {
    @Id
    @Column(name = "stage_id")
    private Long stageId;

    @OneToOne(fetch = FetchType.LAZY, optional = false) @MapsId
    @JoinColumn(name = "stage_id", nullable = false)
    private Stage stage;

    @Column(nullable = false)
    private double weight = 1.0;

    @Column(nullable = false)
    private int maxScore = 10;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JuryRevealMode juryRevealMode = JuryRevealMode.IMMEDIATE;

    @Column(nullable = false)
    private boolean showJudgeNames = true;
}
