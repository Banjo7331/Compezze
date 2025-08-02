package com.cmze.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "room_results")
public class RoomResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String room;

    @OneToMany(mappedBy = "surveyRoomResult", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<SurveyAttempt> questionRoomResult;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "surveyId")
    private SurveyForm survey;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userId")
    private User user;
}
