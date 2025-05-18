package com.cmze.usecase;

import com.cmze.repository.ContestRepository;
import com.cmze.shared.MinioService;

@UseCase
public class SubmitEntryForContestUseCase {

    private final MinioService minioService;
    private final ContestRepository contestRepository;

    public SubmitEntryForContestUseCase(MinioService minioService, ContestRepository contestRepository) {
        this.minioService = minioService;
        this.contestRepository = contestRepository;
    }

    public void execute() {
        contestRepository.save()
    }




}
