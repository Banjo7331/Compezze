package com.cmze.internal.service.voting.strategy.impl;

import com.cmze.entity.Stage;
import com.cmze.enums.StageType;
import com.cmze.internal.service.voting.strategy.VotingStrategy;
import com.cmze.repository.*;
import com.cmze.shared.ActionResult;
import com.cmze.spi.RoomGateway;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

@Component
public class JuryVotingStrategy implements VotingStrategy {

//    private final ParticipantRepository participantRepo;
//    private final VoteMarkerRepository voteRepo;
//    private final StagePublicConfigRepository weightRepo;
//    private final SubmissionRepository submissionRepo;
//    private final RoomRepository roomRepo;
//    private final RoomGateway roomGateway;
//
//    public JuryVotingStrategy(ParticipantRepository participantRepo,
//                              VoteMarkerRepository voteRepo,
//                              StagePublicConfigRepository weightRepo,
//                              SubmissionRepository submissionRepo,
//                              RoomRepository roomRepo,
//                              RoomGateway roomGateway) {
//        this.participantRepo = participantRepo;
//        this.voteRepo = voteRepo;
//        this.weightRepo = weightRepo;
//        this.submissionRepo = submissionRepo;
//        this.roomRepo = roomRepo;
//        this.roomGateway = roomGateway;
//    }
//
//    @Override
//    public StageType type() { return StageType.JURY_VOTING; }
//
//    @Transactional
//    @Override
//    public ActionResult<Void> submit(Stage stage, String userId, VoteCommand cmd) {
//        if (cmd.getEntryId() == null || cmd.getEntryId().isBlank()) {
//            return ActionResult.failure(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "entryId required."));
//        }
//        if (cmd.getScore() == null) {
//            return ActionResult.failure(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "score required."));
//        }
//
//        // 1) weryfikacja: user jest uczestnikiem konkursu i ma rolę JURY
//        Optional<Participant> pOpt = participantRepo.findByContest_IdAndUserId(stage.getContest().getId(), userId);
//        if (pOpt.isEmpty()) {
//            return ActionResult.failure(ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "Not a participant of this contest."));
//        }
//        Participant judge = pOpt.get();
//        Set<com.cmze.enums.ContestRole> roles = judge.getRoles();
//        if (roles == null || !roles.contains(ContestRole.JURY)) {
//            return ActionResult.failure(ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "User is not a jury member."));
//        }
//
//        // 2) walidacja zakresu oceny wg konfiguracji etapu
//        int max = weightRepo.findById(stage.getId()).map(StageWeightConfig::getJuryMaxScore).orElse(10);
//        if (cmd.getScore() < 0 || cmd.getScore() > max) {
//            return ActionResult.failure(ProblemDetail.forStatusAndDetail(
//                    HttpStatus.BAD_REQUEST, "Score out of range (0.." + max + ")."));
//        }
//
//        // 3) idempotencja: 1 ocena sędziego na entry w danym etapie
//        int inserted = voteRepo.tryInsert(stage.getId(), judge.getId(), cmd.getEntryId(), cmd.getScore());
//        if (inserted == 0) {
//            return ActionResult.failure(ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, "Score already submitted."));
//        }
//
//        // 4) delta do sumy submission.overallScore (waga × score)
//        double w = weightRepo.findById(stage.getId()).map(StageWeightConfig::getJuryWeight).orElse(1.0);
//        Submission sub = submissionRepo.findById(cmd.getEntryId()).orElse(null);
//        if (sub != null) {
//            sub.setOverallScore(sub.getOverallScore() + (w * cmd.getScore()));
//            submissionRepo.save(sub);
//        }
//
//        // 5) suma jury dla UI (SUM(score) w tym etapie dla tego entry)
//        long newTotal = voteRepo.sumJuryFor(stage.getId(), cmd.getEntryId());
//
//        // 6) broadcast (jawny sędzia – bierzemy nazwę z Participant; dopasuj pole)
//        String judgeName = (judge.getDisplayName() != null && !judge.getDisplayName().isBlank())
//                ? judge.getDisplayName()
//                : (judge.getNickname() != null ? judge.getNickname() : "Jury");
//
//        String roomKey = roomRepo.findByContest_Id(stage.getContest().getId())
//                .map(Room::getRoomKey).orElse("room:" + stage.getContest().getId());
//
//        roomGateway.publishRoomEvent(RoomEvent.of(
//                RoomEvent.Type.JURY_SCORE_ACCEPTED,
//                roomKey,
//                Map.of(
//                        "contestId", stage.getContest().getId(),
//                        "stageId", stage.getId(),
//                        "position", stage.getPosition(),
//                        "entryId", cmd.getEntryId(),
//                        "judge", Map.of("id", judge.getUserId(), "name", judgeName),
//                        "scoreDelta", cmd.getScore(),
//                        "newTotal", newTotal
//                )
//        ));
//        return ActionResult.success(null);
//    }
}