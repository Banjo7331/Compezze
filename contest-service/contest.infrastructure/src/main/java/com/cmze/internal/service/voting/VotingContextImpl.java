package com.cmze.internal.service.voting;

import com.cmze.entity.Contest;
import com.cmze.entity.Room;
import com.cmze.entity.Stage;
import com.cmze.enums.ContestStatus;
import com.cmze.enums.StageType;
import com.cmze.spi.VotingContext;
import com.cmze.repository.ContestRepository;
import com.cmze.repository.RoomRepository;
import com.cmze.repository.StageRepository;
import com.cmze.request.VoteCommand;
import com.cmze.shared.ActionResult;
import com.cmze.internal.service.voting.strategy.VotingStrategy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

@Component
public class VotingContextImpl implements VotingContext {

//    private final Map<StageType, VotingStrategy> strategies;
//    private final ContestRepository contestRepository;
//    private final RoomRepository roomRepository;
//    private final StageRepository stageRepository;
//
//    public VotingContextImpl(List<VotingStrategy> strategyBeans,
//                             ContestRepository contestRepository,
//                             RoomRepository roomRepository,
//                             StageRepository stageRepository) {
//        this.strategies = new EnumMap<>(
//                strategyBeans.stream().collect(toMap(VotingStrategy::type, Function.identity()))
//        );
//        this.contestRepository = contestRepository;
//        this.roomRepository = roomRepository;
//        this.stageRepository = stageRepository;
//    }
//
//    @Override
//    public ActionResult<Void> submitForStage(String contestId, long stageId, String userId, VoteCommand cmd) {
//        // 1) walidacja konkursu
//        Contest contest = contestRepository.findById(contestId).orElse(null);
//        if (contest == null) {
//            return ActionResult.failure(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "Contest not found."));
//        }
//        LocalDateTime now = LocalDateTime.now();
//        if (contest.getStatus() == null || contest.getStatus() == ContestStatus.DRAFT) {
//            return ActionResult.failure(ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, "Contest not started."));
//        }
//        if (now.isBefore(contest.getStartDate()) || now.isAfter(contest.getEndDate())) {
//            return ActionResult.failure(ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, "Contest outside time window."));
//        }
//
//        // 2) walidacja etapu (musi należeć do konkursu)
//        Stage stage = stageRepository.findById(stageId).orElse(null);
//        if (stage == null || stage.getContest() == null || !contestId.equals(stage.getContest().getId())) {
//            return ActionResult.failure(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "Stage not found in this contest."));
//        }
//
//        // 3) wybór strategii
//        var strategy = strategies.get(stage.getType());
//        if (strategy == null) {
//            return ActionResult.failure(ProblemDetail.forStatusAndDetail(
//                    HttpStatus.NOT_IMPLEMENTED, "No strategy for stage type: " + stage.getType()));
//        }
//
//        // 4) delegacja do strategii
//        try {
//            return strategy.submit(stage, userId, cmd);
//        } catch (Exception ex) {
//            return ActionResult.failure(ProblemDetail.forStatusAndDetail(
//                    HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage() != null ? ex.getMessage() : "Voting failed."));
//        }
//    }
}
