package com.cmze.entity;

import com.cmze.enums.ContestCategory;
import com.cmze.enums.VotingType;
import jakarta.persistence.*;

import jakarta.validation.constraints.*;
import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "contests")
public class Contest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotBlank(message = "Contest name must not be blank")
    @Size(min = 3, max = 100, message = "Contest name must be between 3 and 100 characters")
    private String name;

    @NotBlank(message = "Contest description must not be blank")
    @Size(max = 1000, message = "Description can be up to 1000 characters")
    private String description;

    @Size(max = 255, message = "Location can be up to 255 characters")
    private String location;

    @NotNull()
    @Enumerated(EnumType.STRING)
    private ContestCategory contestCategory;

    @Min(value = 1, message = "Participant limit must be greater than 0")
    private Integer participantLimit;

    @NotNull()
    @FutureOrPresent(message = "Start date cannot be in the past")
    private LocalDateTime startDate;

    @NotNull()
    @Future(message = "End date must be in the future")
    private LocalDateTime endDate;

    @NotNull()
    private boolean isPrivate = false;
    @NotNull()
    private boolean publishToSocialMedia = false;
    @NotNull()
    private boolean hasPreliminaryStage = false;

    @NotNull()
    @Enumerated(EnumType.STRING)
    private VotingType votingType;

    @NotNull()
    private boolean isOpen = true;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Submission> submissions;

    private boolean contentVerified = false;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "organizer_id", nullable = false)
    private User user;

    @ManyToOne
    private User organizer;
}

