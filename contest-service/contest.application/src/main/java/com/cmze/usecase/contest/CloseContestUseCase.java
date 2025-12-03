package com.cmze.usecase.contest;

import com.cmze.entity.Contest;
import com.cmze.repository.ContestRepository;
import com.cmze.shared.ActionResult;
import com.cmze.usecase.UseCase;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

@UseCase
public class CloseContestUseCase {

    private final ContestRepository contestRepository;

    public CloseContestUseCase(ContestRepository contestRepository) {
        this.contestRepository = contestRepository;
    }

//    @Transactional
//    public ActionResult<Void> execute(String contestId, String organizerId) {
//        var opt = contestRepository.findById(contestId);
//        if (opt.isEmpty()) {
//            return ActionResult.failure(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "Contest not found."));
//        }
//        Contest c = opt.get();
//
//        if (c.getOrganizerId() != null && !c.getOrganizerId().equals(organizerId)) {
//            return ActionResult.failure(ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "Not the organizer."));
//        }
//
//        // Idempotentnie – jeśli już po endDate lub zamknięty wcześniej
//        if (c.getClosedAt() != null || (c.getEndDate() != null && java.time.LocalDateTime.now().isAfter(c.getEndDate()))) {
//            return ActionResult.success(null);
//        }
//
//        c.setOpen(false); // zatrzymaj zgłoszenia/głosowania
//        c.setClosedAt(java.time.LocalDateTime.now());
//
//        contestRepository.save(c);
//        return ActionResult.success(null);
//    }
}