package com.cmze.entity.stagesettings;

import com.cmze.entity.Stage;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "stage_public") // Tabela na dodatkowe pola
@DiscriminatorValue("PUBLIC_VOTE") // Wartość z enuma StageType
public class PublicVoteStage extends Stage {

    @Column(nullable = false)
    private double weight = 1.0;

    @Column(nullable = false)
    private int maxScore = 1;


}