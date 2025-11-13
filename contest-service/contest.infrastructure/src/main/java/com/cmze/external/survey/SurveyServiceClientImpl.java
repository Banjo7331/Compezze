package com.cmze.external.survey;

import com.cmze.spi.survey.SurveyRoomDto;
import com.cmze.spi.survey.SurveyServiceClient;
import feign.FeignException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class SurveyServiceClientImpl implements SurveyServiceClient {

    private final InternalSurveyApi internalApi;

    public SurveyServiceClientImpl(InternalSurveyApi internalApi) {
        this.internalApi = internalApi;
    }

    @Override
    public SurveyRoomDto createRoom(Long surveyFormId, int maxUsers) {
        try {
            ResponseEntity<SurveyRoomDto> response = internalApi.createRoom(surveyFormId, maxUsers);
            return response.getBody();
        } catch (FeignException.BadRequest | FeignException.NotFound ex) {
            throw new RuntimeException("Failed to create survey room: " + ex.getMessage(), ex);
        } catch (Exception e) {
            throw new RuntimeException("Survey service is unavailable or failed: " + e.getMessage(), e);
        }
    }
}
