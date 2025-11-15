package com.cmze.controller;

import com.cmze.request.CreateSurveyRoomRequest;
import com.cmze.request.SubmitSurveyAttemptRequest.SubmitSurveyAttemptRequest;
import com.cmze.shared.ActionResult;
import com.cmze.usecase.CloseSurveyRoomUseCase;
import com.cmze.usecase.CreateSurveyRoomUseCase;
import com.cmze.usecase.JoinSurveyRoomUseCase;
import com.cmze.usecase.SubmitSurveyAttemptUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.UUID;

@RestController
@RequestMapping("survey/room")
public class RoomController {

    private final CreateSurveyRoomUseCase createSurveyRoomUseCase;
    private final JoinSurveyRoomUseCase joinSurveyRoomUseCase;
    private final SubmitSurveyAttemptUseCase submitSurveyAttemptUseCase;
    private final CloseSurveyRoomUseCase closeSurveyRoomUseCase;

    public RoomController(CreateSurveyRoomUseCase createSurveyRoomUseCase,
                          JoinSurveyRoomUseCase joinSurveyRoomUseCase,
                          SubmitSurveyAttemptUseCase submitSurveyAttemptUseCase,
                          CloseSurveyRoomUseCase closeSurveyRoomUseCase) {
        this.createSurveyRoomUseCase = createSurveyRoomUseCase;
        this.joinSurveyRoomUseCase = joinSurveyRoomUseCase;
        this.submitSurveyAttemptUseCase = submitSurveyAttemptUseCase;
        this.closeSurveyRoomUseCase = closeSurveyRoomUseCase;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createRoom(
            @RequestBody @Valid CreateSurveyRoomRequest request,
            Authentication authentication) {

        UUID creatorUserId = (UUID) authentication.getPrincipal();

        var result = createSurveyRoomUseCase.execute(request, creatorUserId);

        return result.toResponseEntity(HttpStatus.CREATED);
    }

    @PostMapping("/{roomId}/join")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> joinRoom(
            @PathVariable UUID roomId,
            Authentication authentication
    ) {
        UUID participantUserId = (UUID) authentication.getPrincipal();

        var result = joinSurveyRoomUseCase.execute(roomId, participantUserId);

        return result.toResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/{roomId}/submit")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> submitAnswers(
            @PathVariable UUID roomId,
            @RequestBody @Valid SubmitSurveyAttemptRequest request,
            Authentication authentication
    ) {
        UUID participantUserId = (UUID) authentication.getPrincipal();
        var result = submitSurveyAttemptUseCase.execute(roomId, participantUserId, request);

        return result.toResponseEntity(HttpStatus.CREATED);
    }

    @PostMapping("/{roomId}/close")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> closeRoom(
                                        @PathVariable UUID roomId,
                                        Authentication authentication
    ) {
        UUID hostUserId = (UUID) authentication.getPrincipal();
        var result = closeSurveyRoomUseCase.execute(roomId, hostUserId);

        return result.toResponseEntity(HttpStatus.OK);
    }
}
