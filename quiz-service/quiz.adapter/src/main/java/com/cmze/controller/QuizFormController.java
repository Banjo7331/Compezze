package com.cmze.controller;

import com.cmze.response.QuizFormResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("form")
public class QuizFormController {

    @PostMapping()
    public ResponseEntity<QuizFormResponse> createQuiz(){

    }

    @GetMapping("{id}/question")
    public ResponseEntity<QuizFormResponse> createQuiz(){

    }
}
