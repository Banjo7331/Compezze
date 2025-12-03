package com.cmze.usecase.contest;

import com.cmze.entity.Contest;
import com.cmze.entity.Participant;
import com.cmze.entity.Room;
import com.cmze.enums.ContestStatus;
import com.cmze.repository.ContestRepository;
import com.cmze.repository.ParticipantRepository;
import com.cmze.repository.RoomRepository;
import com.cmze.response.event.RoomEvent;
import com.cmze.shared.ActionResult;
import com.cmze.spi.RoomGateway;
import com.cmze.usecase.UseCase;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.time.LocalDateTime;
import java.util.UUID;

@UseCase
public class StartContestUseCase {

//    private final ContestRepository contestRepository;
//    private final ParticipantRepository participantRepository;
//    private final RoomRepository roomRepository;
//    private final RoomGateway roomGateway;
//
//    public StartContestUseCase(ContestRepository contestRepository,
//                               ParticipantRepository participantRepository,
//                               RoomRepository roomRepository,
//                               RoomGateway roomGateway) {
//        this.contestRepository = contestRepository;
//        this.participantRepository = participantRepository;
//        this.roomRepository = roomRepository;
//        this.roomGateway = roomGateway;
//    }
//
//    @Transactional
//    public ActionResult<String> execute(String contestId, String requesterUserId) {
//        // 1) Konkurs
//        Contest contest = contestRepository.findById(contestId).orElse(null);
//        if (contest == null) {
//            return ActionResult.failure(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "Contest not found."));
//        }
//
//        // 2) Uprawnienia (Organizer/Moderator)
//        Optional<Participant> pOpt = participantRepository.findByContest_IdAndUserId(contestId, requesterUserId);
//        if (pOpt.isEmpty()) {
//            return ActionResult.failure(ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "Not a participant."));
//        }
//        Set<ContestRole> roles = Optional.ofNullable(pOpt.get().getRoles()).orElse(Set.of());
//        boolean allowed = roles.contains(ContestRole.Organizer) || roles.contains(ContestRole.Moderator);
//        if (!allowed) {
//            return ActionResult.failure(ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "Not allowed."));
//        }
//
//        // 3) Walidacja statusu + okna czasowego
//        ContestStatus status = contest.getStatus(); // enum: CREATED, DRAFT, ACTIVE, FINISHED
//        if (status == ContestStatus.FINISHED) {
//            return ActionResult.failure(ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, "Contest already finished."));
//        }
//
//        LocalDateTime now = LocalDateTime.now();
//        if (contest.getStartDate() != null && now.isBefore(contest.getStartDate())) {
//            return ActionResult.failure(ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, "Contest not started yet."));
//        }
//        if (contest.getEndDate() != null && now.isAfter(contest.getEndDate())) {
//            return ActionResult.failure(ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, "Contest already ended."));
//        }
//
//        // 4) Utworzenie/reaktywacja pokoju
//        Room room = roomRepository.findByContest_Id(contestId).orElseGet(() -> {
//            Room r = new Room();
//            r.setContest(contest);
//            r.setOpenedBy(pOpt.get());
//            r.setActive(true);
//            r.setRoomKey(generateRoomKey()); // <= 120 znaków
//            // currentStagePosition = null (start „przed” pierwszym stage’em)
//            return r;
//        });
//        if (!room.isActive()) {
//            room.setActive(true);
//            room.setOpenedBy(pOpt.get());
//            room.setClosedAt(null);
//        }
//        Room savedRoom = roomRepository.save(room);
//
//        // 5) Zmiana statusu konkursu (idempotentnie)
//        if (status == ContestStatus.CREATED || status == ContestStatus.DRAFT) {
//            contest.setStatus(ContestStatus.ACTIVE);
//            contestRepository.save(contest);
//        }
//
//        // 6) Eventy (po zapisie): najpierw ROOM_READY, potem CONTEST_STARTED
//        Map<String, Object> readyPayload = new HashMap<>();
//        readyPayload.put("contestId", contest.getId());
//        readyPayload.put("roomKey", savedRoom.getRoomKey());
//
//        roomGateway.publishRoomEvent(new RoomEvent(
//                RoomEvent.Type.ROOM_READY,
//                System.currentTimeMillis(),
//                savedRoom.getRoomKey(),
//                readyPayload
//        ));
//
//        Map<String, Object> startedPayload = new HashMap<>();
//        startedPayload.put("contestId", contest.getId());
//
//        roomGateway.publishRoomEvent(new RoomEvent(
//                RoomEvent.Type.CONTEST_STARTED,
//                System.currentTimeMillis(),
//                savedRoom.getRoomKey(),
//                startedPayload
//        ));
//
//        // zwróć roomKey (kontroler może zmapować to na 200/204 – wg potrzeb)
//        return ActionResult.success(savedRoom.getRoomKey());
//    }
//
//    private static String generateRoomKey() {
//        // prosta, deterministyczna długość (<= 120)
//        return "room_" + UUID.randomUUID().toString().replace("-", "");
//    }
}


