package com.cmze.response;

import com.cmze.enums.ContestCategory;
import com.cmze.enums.ContestStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetContestDetailsResponse {

    private String id;
    private String name;
    private String description;
    private String location;
    private ContestCategory category;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private ContestStatus status;
    private int participantLimit;
    private boolean isPrivate;


    private List<GetStageDetailsResponse> stages;
}
