package com.cmze.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("contest/{contestId}/participant")
public class ParticipantController {

//    @PostMapping("/{id}/submissions/review")
//    public ResponseEntity<?> review(@PathVariable("id") String contestId,
//                                    @RequestBody @Valid ReviewSubmissionRequest body) {
//        // jeśli chcesz weryfikować rolę reviewera – zrób to tutaj lub w filtrze/security
//        var res = reviewSubmissionUseCase.execute(contestId, body);
//        return res.toResponseEntity(HttpStatus.NO_CONTENT);
//    }
}
