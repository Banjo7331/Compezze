package com.cmze.controller;

import com.cmze.request.CreateSurveyFormRequest;
import com.cmze.response.CreateSurveyFormResponse;
import com.cmze.response.GetSurveyFormSummaryResponse;
import com.cmze.shared.ActionResult;
import com.cmze.usecase.CreateSurveyFormUseCase;
import com.cmze.usecase.DeleteSurveyFormUseCase;
import com.cmze.usecase.GetAllSurveyFormsUseCase;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("survey/form")
public class SurveyFormController {

    private final CreateSurveyFormUseCase createSurveyFormUseCase;
    private final GetAllSurveyFormsUseCase getAllSurveyFormsUseCase;
    private final DeleteSurveyFormUseCase deleteSurveyFormUseCase;

    public SurveyFormController(CreateSurveyFormUseCase createSurveyFormUseCase,
                                GetAllSurveyFormsUseCase getAllSurveyFormsUseCase,
                                DeleteSurveyFormUseCase deleteSurveyFormUseCase) {
        this.createSurveyFormUseCase = createSurveyFormUseCase;
        this.getAllSurveyFormsUseCase = getAllSurveyFormsUseCase;
        this.deleteSurveyFormUseCase = deleteSurveyFormUseCase;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createSurveyForm(
            @RequestBody @Valid CreateSurveyFormRequest request,
            Authentication authentication
    ) {
        UUID ownerUserId = (UUID) authentication.getPrincipal();

        var result = createSurveyFormUseCase.execute(request, ownerUserId);

        return result.toResponseEntity(HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAllSurveyForms(Authentication authentication,
                                               @PageableDefault(size = 20, sort = "title") Pageable pageable
    ) {
        UUID currentUserId = (UUID) authentication.getPrincipal();

        var result = getAllSurveyFormsUseCase.execute(currentUserId, pageable);

        return result.toResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteSurveyForm(@PathVariable Long id, Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();

        var result = deleteSurveyFormUseCase.execute(id, userId);

        return result.toResponseEntity(HttpStatus.NO_CONTENT);
    }
}
