package com.cmze.request;

import com.cmze.enums.SubmissionStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewSubmissionRequest {
    @NotBlank
    private String submissionId;
    @NotNull
    private SubmissionStatus status;
    private String comment;
}
