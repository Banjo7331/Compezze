package com.cmze.internal.service.voting.strategy.impl;

import com.cmze.enums.StageType;
import com.cmze.internal.service.voting.strategy.VotingStrategy;
import com.cmze.repository.RoomRepository;
import com.cmze.repository.StagePublicConfigRepository;
import com.cmze.repository.SubmissionRepository;
import com.cmze.repository.VoteMarkerRepository;
import com.cmze.spi.RoomGateway;
import org.springframework.stereotype.Component;

@Component
public class PublicVotingStrategy implements VotingStrategy {

//    private final VoteMarkerRepository voteRepo;
//    private final StagePublicConfigRepository weightRepo;
//    private final SubmissionRepository submissionRepo;
//    private final RoomRepository roomRepo;
//    private final RoomGateway roomGateway;
//
//    public PublicVotingStrategy(VoteMarkerRepository voteRepo,
//                                StagePublicConfigRepository weightRepo,
//                                SubmissionRepository submissionRepo,
//                                RoomRepository roomRepo,
//                                RoomGateway roomGateway) {
//        this.voteRepo = voteRepo;
//        this.weightRepo = weightRepo;
//        this.submissionRepo = submissionRepo;
//        this.roomRepo = roomRepo;
//        this.roomGateway = roomGateway;
//    }
//
//    @Override
//    public StageType type() { return StageType.PUBLIC_VOTING; }
//
//    @Transactional
//    @Override
//    public ActionResult<Void> submit(Stage stage, String userId, VoteCommand cmd) {
//        if (cmd.getEntryId() == null || cmd.getEntryId().isBlank())
//            return ActionResult.failure(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "entryId required."));
//
//        // znajdź Participant po (contestId, userId)
//        // zakładam, że masz ParticipantRepository:
//        // var part = participantRepo.findByContest_IdAndUserId(stage.getContest().getId(), userId).orElse(null);
//        // if (part == null) return failure(403,"Not a participant");
//        // Dla czytelności pseudo:
//        Participant part = /* ... */ null;
//
//        int inserted = voteRepo.tryInsert(stage.getId(), part.getId(), cmd.getEntryId(), null);
//        if (inserted == 0)
//            return ActionResult.failure(ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, "Already voted."));
//
//        double w = weightRepo.findById(stage.getId()).map(StageWeightConfig::getPublicWeight).orElse(1.0);
//        var sub = submissionRepo.findById(cmd.getEntryId()).orElse(null);
//        if (sub != null) {
//            sub.setOverallScore(sub.getOverallScore() + w);
//            submissionRepo.save(sub);
//        }
//
//        long newTotal = voteRepo.countPublicFor(stage.getId(), cmd.getEntryId());
//
//        String roomKey = roomRepo.findByContest_Id(stage.getContest().getId())
//                .map(Room::getRoomKey).orElse("room:" + stage.getContest().getId());
//
//        roomGateway.publishRoomEvent(RoomEvent.of(
//                RoomEvent.Type.PUBLIC_VOTE_ACCEPTED,
//                roomKey,
//                Map.of(
//                        "contestId", stage.getContest().getId(),
//                        "stageId", stage.getId(),
//                        "position", stage.getPosition(),
//                        "entryId", cmd.getEntryId(),
//                        "newTotal", newTotal
//                )
//        ));
//        return ActionResult.success(null);
//    }
}
