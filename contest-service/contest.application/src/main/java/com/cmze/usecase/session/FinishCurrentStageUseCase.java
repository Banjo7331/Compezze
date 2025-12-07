package com.cmze.usecase.session;

import com.cmze.repository.ContestRepository;
import com.cmze.repository.ParticipantRepository;
import com.cmze.shared.ActionResult;
import com.cmze.spi.StageSettingsContext;
import com.cmze.usecase.UseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@UseCase
public class FinishCurrentStageUseCase {

    private static final Logger logger = LoggerFactory.getLogger(FinishCurrentStageUseCase.class);

    private final ContestRepository contestRepository;
    private final ParticipantRepository participantRepository;
    private final StageSettingsContext stageContext;
    private final ApplicationEventPublisher eventPublisher;

    public FinishCurrentStageUseCase(final ContestRepository contestRepository,
                                     final ParticipantRepository participantRepository,
                                     final StageSettingsContext stageContext,
                                     final ApplicationEventPublisher eventPublisher) {
        this.contestRepository = contestRepository;
        this.participantRepository = participantRepository;
        this.stageContext = stageContext;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public ActionResult<Void> execute(final Long contestId, final UUID organizerId) {
        try {

            final var contest = contestRepository.findById(contestId)
                    .orElseThrow(() -> new RuntimeException("Contest not found"));

            if (!contest.getOrganizerId().equals(organizerId.toString())) {
                return ActionResult.failure(ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "Not organizer"));
            }

            // 2. Znalezienie aktywnego etapu
            if (contest.getCurrentStageId() == null) {
                return ActionResult.failure(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "No active stage to finish"));
            }

            final var currentStage = contest.getStages().stream()
                    .filter(s -> s.getId().equals(contest.getCurrentStageId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Active stage not found in stages list"));

            logger.info("Finishing stage: {}", currentStage.getName());

            // 3. ZAMKNIĘCIE I POBRANIE WYNIKÓW (Strategia)
            // To wywołuje np. QuizService.closeRoom() i pobiera tabelę wyników
            final var scores = stageContext.finishStage(currentStage);

            // 4. Aktualizacja Punktów Uczestników
            if (!scores.isEmpty()) {
                scores.forEach((userId, points) -> {
                    // Szukamy uczestnika i dodajemy punkty do sumy całkowitej
                    participantRepository.findByContestIdAndUserId(contestIdString, userId.toString())
                            .ifPresent(p -> {
                                p.setTotalScore(p.getTotalScore() + points.longValue());
                                participantRepository.save(p);
                            });
                });
                logger.info("Updated scores for {} participants", scores.size());
            }

            // 5. Aktualizacja Stanu Konkursu (Tryb Przerwy)
            contest.setCurrentStageId(null);
            contestRepository.save(contest);

            // 6. Powiadomienie WebSocket
            // Frontend otrzyma info o przerwie i powinien wyświetlić aktualny ranking globalny
            eventPublisher.publishEvent(new ContestStageChangedEvent(
                    contestIdString,
                    null, // brak aktywnego etapu
                    "PRZERWA / WYNIKI",
                    null,
                    null
            ));

            return ActionResult.success(null);

        } catch (Exception e) {
            logger.error("Error finishing stage for contest {}", contestIdString, e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ActionResult.failure(ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Error finishing stage"));
        }
    }
}
