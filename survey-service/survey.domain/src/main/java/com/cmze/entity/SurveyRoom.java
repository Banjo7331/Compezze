package com.cmze.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "room_results")
public class SurveyRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToMany(mappedBy = "roomResult", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SurveyAttempt> surveyAttempts = new ArrayList<>();

    @OneToMany(mappedBy = "surveyRoom", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SurveyEntrant> participants = new ArrayList<>();

    @Column(name = "max_participants")
    private Integer maxParticipants;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_form_id")
    private SurveyForm survey;

    @Column(name = "user_id", nullable = false)
    private UUID userId;
}
