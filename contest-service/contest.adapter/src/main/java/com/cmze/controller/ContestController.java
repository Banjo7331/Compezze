package com.cmze.controller;

import com.cmze.request.CreateContestRequest;
import com.cmze.usecase.contest.CloseContestUseCase;
import com.cmze.usecase.contest.CreateContestUseCase;
import com.cmze.usecase.contest.StartContestUseCase;
import com.cmze.usecase.participant.SubmitEntryForContestUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("contest")
public class ContestController {

    private final CreateContestUseCase createContestUseCase;
    private final SubmitEntryForContestUseCase submitEntryForContestUseCase;
    private final CloseContestUseCase closeContestUseCase;
    private final StartContestUseCase startContestUseCase;

    public ContestController(CreateContestUseCase createContestUseCase,
                             SubmitEntryForContestUseCase submitEntryForContestUseCase,
                             CloseContestUseCase closeContestUseCase,
                             StartContestUseCase startContestUseCase
    ) {
        this.createContestUseCase = createContestUseCase;
        this.submitEntryForContestUseCase = submitEntryForContestUseCase;
        this.closeContestUseCase = closeContestUseCase;
        this.startContestUseCase = startContestUseCase;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createContest(@RequestPart("payload") @Valid CreateContestRequest request,
                                           @RequestPart("image") MultipartFile image,
                                           @RequestHeader(name="X-User-Id") String organizerId
    ){
        var result = createContestUseCase.execute(request, image, organizerId);
        return result.toResponseEntity(HttpStatus.CREATED);
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<?> start(@PathVariable String id,
                                   @RequestHeader("X-User-Id") String userId) {
        var result = startContestUseCase.execute(id, userId);
        // Use case emituje CONTEST_STARTED przez WS/SSE; REST może zwrócić 204.
        return result.toResponseEntity(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{id}/close")
    public ResponseEntity<?> close(@PathVariable String id,
                                   @RequestHeader("X-User-Id") String organizerId
    ) {
        var result = closeContestUseCase.execute(id, organizerId);
        return result.toResponseEntity(HttpStatus.NO_CONTENT);
    }

}
