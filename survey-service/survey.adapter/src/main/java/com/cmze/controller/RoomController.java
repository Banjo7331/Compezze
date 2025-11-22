package com.cmze.controller;

import com.cmze.request.CreateSurveyRoomRequest;
import com.cmze.request.GenerateRoomInvitesRequest;
import com.cmze.request.JoinSurveyRoomRequest;
import com.cmze.request.SubmitSurveyAttemptRequest.SubmitSurveyAttemptRequest;
import com.cmze.shared.ActionResult;
import com.cmze.usecase.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("survey/room")
public class RoomController {

    private final CreateSurveyRoomUseCase createSurveyRoomUseCase;
    private final JoinSurveyRoomUseCase joinSurveyRoomUseCase;
    private final SubmitSurveyAttemptUseCase submitSurveyAttemptUseCase;
    private final CloseSurveyRoomUseCase closeSurveyRoomUseCase;
    private final GetAllActiveSurveyRoomsUseCase getAllActiveSurveyRoomsUseCase;
    private final InviteUsersForSurveyRoomUseCase inviteUsersForSurveyRoomUseCase;
    private final GetSurveyRoomDetailsUseCase getSurveyRoomDetailsUseCase;

    public RoomController(CreateSurveyRoomUseCase createSurveyRoomUseCase,
                          JoinSurveyRoomUseCase joinSurveyRoomUseCase,
                          SubmitSurveyAttemptUseCase submitSurveyAttemptUseCase,
                          CloseSurveyRoomUseCase closeSurveyRoomUseCase,
                          GetAllActiveSurveyRoomsUseCase getAllActiveSurveyRoomsUseCase,
                          InviteUsersForSurveyRoomUseCase inviteUsersForSurveyRoomUseCase,
                          GetSurveyRoomDetailsUseCase getSurveyRoomDetailsUseCase) {
        this.createSurveyRoomUseCase = createSurveyRoomUseCase;
        this.joinSurveyRoomUseCase = joinSurveyRoomUseCase;
        this.submitSurveyAttemptUseCase = submitSurveyAttemptUseCase;
        this.closeSurveyRoomUseCase = closeSurveyRoomUseCase;
        this.getAllActiveSurveyRoomsUseCase = getAllActiveSurveyRoomsUseCase;
        this.inviteUsersForSurveyRoomUseCase = inviteUsersForSurveyRoomUseCase;
        this.getSurveyRoomDetailsUseCase = getSurveyRoomDetailsUseCase;
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

    @GetMapping("/{roomId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getRoomDetails(@PathVariable UUID roomId) {
        var result = getSurveyRoomDetailsUseCase.execute(roomId);
        return result.toResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/active")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getActiveRooms(@PageableDefault(size = 20) Pageable pageable) {
        var result = getAllActiveSurveyRoomsUseCase.execute(pageable);
        return result.toResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/{roomId}/join")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> joinRoom(
            @PathVariable UUID roomId,
            @RequestBody(required = false) JoinSurveyRoomRequest request,
            Authentication authentication
    ) {
        UUID participantUserId = (UUID) authentication.getPrincipal();

        var result = joinSurveyRoomUseCase.execute(roomId, participantUserId, request);

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

    @PostMapping("/{roomId}/invites")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> generateInvites(
            @PathVariable UUID roomId,
            @RequestBody GenerateRoomInvitesRequest targetUserIds,
            Authentication authentication
    ) {
        UUID hostId = (UUID) authentication.getPrincipal();

        var result = inviteUsersForSurveyRoomUseCase.execute(roomId, targetUserIds, hostId);

        return result.toResponseEntity(HttpStatus.CREATED);
    }
}
