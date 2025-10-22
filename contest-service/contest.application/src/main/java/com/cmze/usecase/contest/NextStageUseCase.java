package com.cmze.usecase.contest;

import com.cmze.entity.Contest;
import com.cmze.entity.Participant;
import com.cmze.entity.Room;
import com.cmze.entity.Stage;
import com.cmze.enums.ContestRole;
import com.cmze.enums.ContestStatus;
import com.cmze.repository.*;
import com.cmze.response.event.RoomEvent;
import com.cmze.shared.ActionResult;
import com.cmze.spi.RoomGateway;
import com.cmze.usecase.UseCase;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@UseCase
public class NextStageUseCase {

    private final ContestRepository contestRepo;
    private final StageRepository stageRepo;
    private final RoomRepository roomRepo;
    private final ParticipantRepository participantRepo;
    private final StagePublicConfigRepository publicCfgRepo;
    private final StageJuryConfigRepository juryCfgRepo;
    private final RoomGateway roomGateway;

    public NextStageUseCase(ContestRepository contestRepo,
                            StageRepository stageRepo,
                            RoomRepository roomRepo,
                            ParticipantRepository participantRepo,
                            StagePublicConfigRepository publicCfgRepo,
                            StageJuryConfigRepository juryCfgRepo,
                            RoomGateway roomGateway) {
        this.contestRepo = contestRepo;
        this.stageRepo = stageRepo;
        this.roomRepo = roomRepo;
        this.participantRepo = participantRepo;
        this.publicCfgRepo = publicCfgRepo;
        this.juryCfgRepo = juryCfgRepo;
        this.roomGateway = roomGateway;
    }

    @Transactional
    public ActionResult<Void> execute(String contestId, String requesterUserId) {
        // 0) uprawnienia: tylko Organizer lub Moderator
        Participant user = participantRepo.findByContest_IdAndUserId(contestId, requesterUserId);
        if (user.isEmpty()) {
            return ActionResult.failure(ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "Not a participant."));
        }
        var roles = Optional.ofNullable(pOpt.get().getRoles()).orElse(Set.of());
        boolean allowed = roles.contains(ContestRole.Organizer) || roles.contains(ContestRole.Moderator);
        if (!allowed) {
            return ActionResult.failure(ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "Not allowed."));
        }

        // 1) walidacje konkursu
        Contest contest = contestRepo.findById(contestId).orElse(null);
        if (contest == null)
            return ActionResult.failure(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "Contest not found."));

        var now = LocalDateTime.now();
        if (contest.getStatus() == null || contest.getStatus() == ContestStatus.DRAFT)
            return ActionResult.failure(ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, "Contest not started."));
        if (now.isBefore(contest.getStartDate()) || now.isAfter(contest.getEndDate()))
            return ActionResult.failure(ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, "Contest outside time window."));

        // 2) pokój
        Room room = roomRepo.findByContest_Id(contestId).orElse(null);
        if (room == null)
            return ActionResult.failure(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "Room not found."));

        Integer currentPos = room.getCurrentStagePosition();

        // 3) wybór następnego stage
        final Stage nextStage = (currentPos == null)
                ? stageRepo.findFirstByContest_IdOrderByPositionAsc(contestId).orElse(null)
                : stageRepo.findFirstByContest_IdAndPositionGreaterThanOrderByPositionAsc(contestId, currentPos).orElse(null);

        if (nextStage == null) {
            return ActionResult.failure(ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, "No next stage."));
        }

        // 4) WALIDACJA/ZAŁADOWANIE KONFIGURACJI wg typu
        Map<String, Object> configPayload = new HashMap<>();
        var type = nextStage.getType();

        if (type == null) {
            return ActionResult.failure(ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, "Stage type not set."));
        }

        switch (type) {
            case PUBLIC -> {
                StagePublicConfig cfg = publicCfgRepo.findById(nextStage.getId()).orElse(null);
                if (cfg == null) {
                    return ActionResult.failure(ProblemDetail.forStatusAndDetail(
                            HttpStatus.CONFLICT,
                            "Missing public stage config for stageId=" + nextStage.getId()
                    ));
                }
                configPayload.put("weight", cfg.getWeight());
                configPayload.put("maxScore", cfg.getMaxScore()); // u Ciebie zawsze 1 – ale zachowujemy spójne API
            }
            case JURY -> {
                StageJuryConfig cfg = juryCfgRepo.findById(nextStage.getId()).orElse(null);
                if (cfg == null) {
                    return ActionResult.failure(ProblemDetail.forStatusAndDetail(
                            HttpStatus.CONFLICT,
                            "Missing jury stage config for stageId=" + nextStage.getId()
                    ));
                }
                configPayload.put("weight", cfg.getWeight());
                configPayload.put("maxScore", cfg.getMaxScore());
                configPayload.put("juryRevealMode", cfg.getJuryRevealMode().name());
                configPayload.put("showJudgeNames", cfg.isShowJudgeNames());
            }
            // jeśli masz więcej typów – dodaj analogiczne gałęzie:
            // case SOMETHING_ELSE -> { ... }
            default -> {
                // domyślnie nie wymagamy konfiguracji (lub zmień na błąd, jeśli każdy typ MUSI mieć wiersz)
                // return ActionResult.failure(ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, "Unsupported stage type: " + type));
            }
        }

        // 5) przełączenie
        room.setCurrentStagePosition(nextStage.getPosition());
        roomRepo.save(room);

        // 6) STAGE_CHANGED – payload rozszerzony o config
        Map<String, Object> payload = new HashMap<>();
        payload.put("contestId", contestId);
        payload.put("stageId", nextStage.getId());
        payload.put("position", nextStage.getPosition());
        payload.put("type", type.name());
        payload.put("name", nextStage.getName());
        payload.put("durationMinutes", nextStage.getDurationMinutes());
        if (!configPayload.isEmpty()) {
            payload.put("config", configPayload);
        }

        roomGateway.publishRoomEvent(new RoomEvent(
                RoomEvent.Type.STAGE_CHANGED,
                System.currentTimeMillis(),
                room.getRoomKey(),
                payload
        ));

        return ActionResult.success(null);
    }
}
