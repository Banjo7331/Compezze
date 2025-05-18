package com.cmze.usecase;

import com.cmze.entity.SurveyForm;
import com.cmze.repository.SurveyFormRepository;
import com.cmze.request.CreateSurveyRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

@UseCase
public class CreateSurveyUseCase {

    @Autowired
    public final SurveyFormRepository surveyFormRepository;
    private final ModelMapper modelMapper;

    public CreateSurveyUseCase(SurveyFormRepository surveyFormRepository, ModelMapper modelMapper) {
        this.surveyFormRepository = surveyFormRepository;
        this.modelMapper = modelMapper;
    }

    public void execute(CreateSurveyRequest createSurveyRequest) {
        SurveyForm survey = modelMapper.map(createSurveyRequest, SurveyForm.class);
        surveyFormRepository.save(survey);
    }
}
