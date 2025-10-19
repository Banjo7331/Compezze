package com.cmze.request;

import com.cmze.enums.ContestCategory;
import com.cmze.enums.SocialPlatform;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateContestRequest {

    @Size(min = 3, max = 100)
    private String name;

    @Size(max = 1000)
    private String description;

    @Size(max = 255)
    private String location;

    private ContestCategory contestCategory;

    @Min(1)
    private Integer participantLimit;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private Boolean isPrivate;
    private Boolean hasPreliminaryStage;
    private VotingType votingType;

    private Set<SocialPlatform> publishTo;
}
