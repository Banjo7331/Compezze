package com.cmze.usecase;

import com.cmze.entity.SurveyForm;
import com.cmze.repository.SurveyFormRepository;
import com.cmze.response.GetSurveyResponse.GetSurveyResponse;
import com.cmze.shared.ActionResult;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

@UseCase
public class GetAllSurveyUseCase {
    private final SurveyFormRepository surveyFormRepository;
    private final ModelMapper modelMapper;

    public GetAllSurveyUseCase(SurveyFormRepository surveyFormRepository, ModelMapper modelMapper) {
        this.surveyFormRepository = surveyFormRepository;
        this.modelMapper = modelMapper;
    }

    public ActionResult<Page<GetSurveyResponse>> execute(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<SurveyForm> surveysPage = surveyFormRepository.findAll(pageable);

            Page<GetSurveyResponse> responsePage = surveysPage.map(
                    survey -> modelMapper.map(survey, GetSurveyResponse.class)
            );

            return ActionResult.success(responsePage);
        } catch (Exception ex) {
            ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to fetch surveys: " + ex.getMessage()
            );
            return ActionResult.failure(problem);
        }
    }
}

