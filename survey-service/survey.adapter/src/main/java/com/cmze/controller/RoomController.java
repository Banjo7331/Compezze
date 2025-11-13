package com.cmze.controller;

import com.cmze.request.CreateSurveyRoomRequest;
import com.cmze.usecase.CreateSurveyRoomUseCase;
import com.cmze.usecase.JoinSurveyRoomUseCase;
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

    public RoomController(CreateSurveyRoomUseCase createSurveyRoomUseCase,
                          JoinSurveyRoomUseCase joinSurveyRoomUseCase) {
        this.createSurveyRoomUseCase = createSurveyRoomUseCase;
        this.joinSurveyRoomUseCase = joinSurveyRoomUseCase;
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
}
