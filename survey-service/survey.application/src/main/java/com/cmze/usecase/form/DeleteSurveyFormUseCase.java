package com.cmze.usecase.form;

import com.cmze.entity.SurveyForm;
import com.cmze.repository.SurveyFormRepository;
import com.cmze.repository.SurveyRoomRepository;
import com.cmze.shared.ActionResult;
import com.cmze.usecase.UseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@UseCase
public class DeleteSurveyFormUseCase {

    private final SurveyFormRepository surveyFormRepository;
    private final SurveyRoomRepository surveyRoomRepository;

    public DeleteSurveyFormUseCase(SurveyFormRepository surveyFormRepository,
                                   SurveyRoomRepository surveyRoomRepository) {
        this.surveyFormRepository = surveyFormRepository;
        this.surveyRoomRepository = surveyRoomRepository;
    }

    @Transactional
    public ActionResult<Void> execute(Long formId, UUID userId) {
        SurveyForm form = surveyFormRepository.findById(formId).orElse(null);

        if (form == null) return ActionResult.failure(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,"Form not found"));

        if (!form.getCreatorId().equals(userId)) {
            return ActionResult.failure(ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "Not authorized"));
        }

        boolean hasActiveRooms = surveyRoomRepository.existsBySurvey_IdAndIsOpenTrue(formId);
        if (hasActiveRooms) {
            return ActionResult.failure(ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, "Cannot delete template while active rooms exist. Close them first."));
        }

        form.setDeleted(true);
        surveyFormRepository.save(form);

        return ActionResult.success(null);
    }
}
