package com.cmze.controller;

import com.cmze.request.ReorderStagesRequest;
import com.cmze.request.UpdateStageRequest;
import com.cmze.usecase.contest.ReorderStagesUseCase;
import com.cmze.usecase.contest.UpdateStageUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("contest/{contestId}/stages")
public class StageController {

    private final ReorderStagesUseCase reorderStagesUseCase;
    private final UpdateStageUseCase updateStageUseCase;

    public StageController(ReorderStagesUseCase reorderStagesUseCase,
                           UpdateStageUseCase updateStageUseCase
    ) {
        this.reorderStagesUseCase = reorderStagesUseCase;
        this.updateStageUseCase = updateStageUseCase;
    }

    @PutMapping("/reorder")
    public ResponseEntity<?> reorderContestStages(@PathVariable String contestId,
                                     @RequestHeader("X-User-Id") String organizerId,
                                     @Valid @RequestBody ReorderStagesRequest request) {
        var result = reorderStagesUseCase.execute(contestId, organizerId, request);
        return result.toResponseEntity(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{stageId}")
    public ResponseEntity<?> updateStage(@PathVariable String contestId,
                                         @PathVariable Long stageId,
                                         @RequestHeader("X-User-Id") String organizerId,
                                         @Valid @RequestBody UpdateStageRequest request) {
        var result = updateStageUseCase.execute(contestId, stageId, organizerId, request);
        return result.toResponseEntity(HttpStatus.OK);
    }
}
