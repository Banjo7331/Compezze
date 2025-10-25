package com.cmze.usecase.submission;

import com.cmze.entity.Submission;
import com.cmze.enums.SubmissionStatus;
import com.cmze.repository.SubmissionRepository;
import com.cmze.response.GetSubmissionResponse;
import com.cmze.shared.ActionResult;
import com.cmze.spi.minio.MinioService;
import com.cmze.usecase.UseCase;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@UseCase
public class ListSubmissionsForReviewUseCase {

    private final ModelMapper modelMapper;
    private final SubmissionRepository submissionRepo;
    private final MinioService minioService;

    public ListSubmissionsForReviewUseCase(ModelMapper modelMapper,SubmissionRepository submissionRepo, MinioService minioService) {
        this.modelMapper = modelMapper;
        this.submissionRepo = submissionRepo;
        this.minioService = minioService;
    }

    @Transactional
    public ActionResult<Page<GetSubmissionResponse>> execute(String contestId,
                                                             SubmissionStatus status,
                                                             int page, int size) {
        var pageable = PageRequest.of(Math.max(0,page), Math.max(1,size), Sort.by("id").descending());
        Page<GetSubmissionResponse> result = submissionRepo
                .findByContest_IdAndStatus(contestId, status, pageable)
                .map(s -> modelMapper.map(s, GetSubmissionResponse.class));
        return ActionResult.success(result);
    }
}