package com.cmze.external.survey;

import com.cmze.configuration.FeignConfig;
import com.cmze.spi.survey.SurveyRoomDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(
        name = "survey-service",
        path = "/surveys",
        configuration = FeignConfig.class
)
public interface InternalSurveyApi {

    @PostMapping("/{surveyFormId}/{maxUsers}/create-room")
    ResponseEntity<SurveyRoomDto> createRoom(
            @PathVariable("surveyFormId") Long surveyFormId,
            @PathVariable("maxUsers") int maxUsers
    );
}
