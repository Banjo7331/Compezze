package com.cmze.controller;

import com.cmze.request.CreateQuizRoomRequest;
import com.cmze.request.JoinQuizRoomRequest;
import com.cmze.request.SubmitQuizAnswerRequest;
import com.cmze.usecase.room.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("quiz/room")
public class RoomController {

    private final CreateQuizRoomUseCase createQuizRoomUseCase;
    private final JoinQuizRoomUseCase joinQuizRoomUseCase;
    private final StartQuizUseCase startQuizUseCase;
    private final SubmitQuizAnswerUseCase submitQuizAnswerUseCase;
    private final FinishCurrentQuestionUseCase finishCurrentQuestionUseCase;

    public RoomController(final CreateQuizRoomUseCase createQuizRoomUseCase,
                          final JoinQuizRoomUseCase joinQuizRoomUseCase,
                          final StartQuizUseCase startQuizUseCase,
                          final SubmitQuizAnswerUseCase submitQuizAnswerUseCase,
                          final FinishCurrentQuestionUseCase finishCurrentQuestionUseCase) {
        this.createQuizRoomUseCase = createQuizRoomUseCase;
        this.joinQuizRoomUseCase = joinQuizRoomUseCase;
        this.startQuizUseCase = startQuizUseCase;
        this.submitQuizAnswerUseCase = submitQuizAnswerUseCase;
        this.finishCurrentQuestionUseCase = finishCurrentQuestionUseCase;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createQuizRoom(
            @RequestBody @Valid final CreateQuizRoomRequest request,
            final Authentication authentication
    ) {
        final var hostId = (UUID) authentication.getPrincipal();
        final var result = createQuizRoomUseCase.execute(request, hostId);

        return result.toResponseEntity(HttpStatus.CREATED);
    }

    @PostMapping("/{roomId}/join")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> joinQuizRoom(
            @PathVariable final UUID roomId,
            @RequestBody(required = false) final JoinQuizRoomRequest request,
            final Authentication authentication
    ) {
        final var userId = (UUID) authentication.getPrincipal();
        final var result = joinQuizRoomUseCase.execute(roomId, userId, request);

        return result.toResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/{roomId}/start")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> startQuiz(
            @PathVariable final UUID roomId,
            final Authentication authentication
    ) {
        final var hostId = (UUID) authentication.getPrincipal();

        final var result = startQuizUseCase.execute(roomId, hostId);

        return result.toResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/{roomId}/submit")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> submitAnswer(
            @PathVariable final UUID roomId,
            @RequestBody @Valid final SubmitQuizAnswerRequest request,
            final Authentication authentication
    ) {
        final var userId = (UUID) authentication.getPrincipal();
        final var result = submitQuizAnswerUseCase.execute(roomId, userId, request);

        return result.toResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/{roomId}/question/finish")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> finishQuestionManually(
            @PathVariable final UUID roomId,
            final Authentication authentication
    ) {
        final var hostId = (UUID) authentication.getPrincipal();

        final var result = finishCurrentQuestionUseCase.execute(roomId, hostId);

        return result.toResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/{roomId}/invites")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> generateInvites(
            @PathVariable final UUID roomId,
            @RequestBody final GenerateRoomInvitesRequest request,
            final Authentication authentication
    ) {
        final var hostId = (UUID) authentication.getPrincipal();

        final var result = inviteUsersForQuizRoomUseCase.execute(roomId, request, hostId);

        return result.toResponseEntity(HttpStatus.CREATED);
    }
}
