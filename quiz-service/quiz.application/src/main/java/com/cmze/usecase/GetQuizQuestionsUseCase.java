package com.cmze.usecase;

import com.cmze.repository.QuestionRepository;
import org.modelmapper.ModelMapper;

@UseCase
public class GetQuizQuestionsUseCase {

    private final QuestionRepository questionRepository;
    private final ModelMapper modelMapper;

    public GetQuizQuestionsUseCase(QuestionRepository questionRepository, ModelMapper modelMapper) {
        this.questionRepository = questionRepository;
        this.modelMapper = modelMapper;
    }

}
