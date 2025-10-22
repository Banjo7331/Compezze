package com.cmze.response;

import com.cmze.entity.Submission;
import com.cmze.enums.SubmissionStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetSubmissionResponse {
    private String id;
    private SubmissionStatus status;
    private String comment;

}
