package com.cmze.entity.stagesettings;

import com.cmze.entity.Stage;
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
@Table(name = "stage_generic")
@DiscriminatorValue("GENERIC")
public class GenericStage extends Stage {
}
