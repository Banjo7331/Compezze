package com.cmze.usecase.form;

import com.cmze.entity.SurveyForm;
import com.cmze.repository.SurveyFormRepository;
import com.cmze.response.MySurveyFormResponse;
import com.cmze.shared.ActionResult;
import com.cmze.usecase.UseCase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@UseCase
public class GetMySurveyFormsUseCase {

    private final SurveyFormRepository surveyFormRepository;

    public GetMySurveyFormsUseCase(SurveyFormRepository surveyFormRepository) {
        this.surveyFormRepository = surveyFormRepository;
    }

    @Transactional(readOnly = true)
    public ActionResult<Page<MySurveyFormResponse>> execute(UUID userId, Pageable pageable) {
        Page<SurveyForm> formsPage = surveyFormRepository.findByCreatorIdAndDeletedFalse(userId, pageable);

        Page<MySurveyFormResponse> dtoPage = formsPage.map(form -> new MySurveyFormResponse(
                form.getId(),
                form.getTitle(),
                form.isPrivate(),
                form.getCreatedAt(),
                form.getQuestions() != null ? form.getQuestions().size() : 0
        ));

        return ActionResult.success(dtoPage);
    }
}
