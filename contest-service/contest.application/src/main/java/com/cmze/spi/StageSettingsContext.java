package com.cmze.spi;

import com.cmze.entity.Stage;
import com.cmze.enums.StageType;
import com.cmze.request.CreateContestRequest;
import com.cmze.response.stagesettings.StageSettingsResponse;
import org.springframework.http.ProblemDetail;

public interface StageSettingsContext {
    ProblemDetail validate(CreateContestRequest.StageRequest dto);
    void apply(CreateContestRequest.StageRequest dto, Stage stage);
    StageSettingsResponse runStage(long stageId, StageType type);
}
