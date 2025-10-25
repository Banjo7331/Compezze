package com.cmze.usecase.contest;

import com.cmze.entity.Contest;
import com.cmze.entity.Participant;
import com.cmze.enums.ContestRole;
import com.cmze.repository.ContestRepository;
import com.cmze.repository.ParticipantRepository;
import com.cmze.response.JoinContestResponse;
import com.cmze.shared.ActionResult;
import com.cmze.usecase.UseCase;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.time.LocalDateTime;

@UseCase
public class JoinContestUseCase {

    private final ContestRepository contestRepository;
    private final ParticipantRepository participantRepository;

    public JoinContestUseCase(ContestRepository contestRepository,
                              ParticipantRepository participantRepository) {
        this.contestRepository = contestRepository;
        this.participantRepository = participantRepository;
    }

    /**
     * Dołącza użytkownika do konkursu jako Competitor.
     * Idempotentne: jeśli użytkownik już jest uczestnikiem, zwraca sukces z istniejącym participantId.
     */
    @Transactional
    public ActionResult<JoinContestResponse> execute(String contestId, String userId) {
        // --- 0) Walidacja wejścia ---
        if (contestId == null || contestId.isBlank()) {
            return ActionResult.failure(ProblemDetail.forStatusAndDetail(
                    HttpStatus.BAD_REQUEST, "contestId is required."));
        }
        if (userId == null || userId.isBlank()) {
            return ActionResult.failure(ProblemDetail.forStatusAndDetail(
                    HttpStatus.BAD_REQUEST, "userId is required."));
        }

        // --- 1) Pobierz konkurs ---
        Contest contest = contestRepository.findById(contestId);
        if (contest == null) {
            return ActionResult.failure(ProblemDetail.forStatusAndDetail(
                    HttpStatus.NOT_FOUND, "Contest not found."));
        }

        // --- 2) Reguły biznesowe dołączenia ---
        // Jeśli masz osobne okno zapisów – podmień warunki na dedykowane pola.
        if (!contest.isOpen()) {
            return ActionResult.failure(ProblemDetail.forStatusAndDetail(
                    HttpStatus.UNPROCESSABLE_ENTITY, "Contest is closed."));
        }
        LocalDateTime now = LocalDateTime.now();

        // --- 3) Idempotencja: czy już jest uczestnikiem? ---
        Participant existing = participantRepository.findsByContestIdAndUserId(contestId, userId);
        if (existing != null) {
            return ActionResult.success(new JoinContestResponse(existing.getId()));
        }

        // --- 4) Limit uczestników ---
        if (contest.getParticipantLimit() != null && contest.getParticipantLimit() > 0) {
            long currentParticipants = participantRepository.countByContestId(contestId);
            if (currentParticipants >= contest.getParticipantLimit()) {
                return ActionResult.failure(ProblemDetail.forStatusAndDetail(
                        HttpStatus.CONFLICT, "Participant limit reached."));
            }
        }

        // --- 5) Utwórz uczestnika ---
        Participant participant = new Participant();
        participant.setContest(contest);
        participant.setUserId(userId);
        participant.setUpdatedAt(now);

        Participant saved = participantRepository.save(participant);

        // --- 6) Zwróć wynik ---
        return ActionResult.success(new JoinContestResponse(saved.getId()));
    }
}
