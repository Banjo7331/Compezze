package com.cmze.usecase.contest;

import com.cmze.entity.Contest;
import com.cmze.entity.Stage;

import com.cmze.entity.stagesettings.*;
import com.cmze.enums.ContestStatus;
import com.cmze.request.CreateContestRequest;
import com.cmze.request.stages.StageRequest;

import com.cmze.repository.ContestRepository;
import com.cmze.spi.minio.MediaLocation;
import com.cmze.spi.minio.MinioService;

import com.cmze.response.CreateContestResponse;
import com.cmze.shared.ActionResult;
import com.cmze.spi.minio.ObjectKeyFactory;
import com.cmze.spi.minio.ObjectMetadata;
import com.cmze.usecase.UseCase;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@UseCase
public class CreateContestUseCase {

    private static final Logger logger = LoggerFactory.getLogger(CreateContestUseCase.class);

    private final ContestRepository contestRepository;
    private final ModelMapper modelMapper;
    private final MinioService minioService;
    private final ObjectKeyFactory objectKeyFactory;

    @Value("${app.media.publicBaseUrl:}")
    private String publicBaseUrl;

    public CreateContestUseCase(ContestRepository contestRepository,
                                ModelMapper modelMapper,
                                MinioService minioService,
                                ObjectKeyFactory objectKeyFactory) {
        this.contestRepository = contestRepository;
        this.modelMapper = modelMapper;
        this.minioService = minioService;
        this.objectKeyFactory = objectKeyFactory;
    }

    @Transactional
    public ActionResult<CreateContestResponse> execute(CreateContestRequest request, String organizerId) {


        if (request.getStartDate().isAfter(request.getEndDate())) {
            return ActionResult.failure(ProblemDetail.forStatusAndDetail(
                    HttpStatus.UNPROCESSABLE_ENTITY, "End date must be after start date."
            ));
        }

        MediaLocation location = null; // Lokalizacja docelowa okładki
        String contentType;
        long imageSize;

        try {

            String templateKey = request.getTemplateId();
            String templateBucket = objectKeyFactory.getPublicBucket();

            location = objectKeyFactory.generateForContestCover(organizerId, templateKey);

            ObjectMetadata templateMetadata = minioService.copyAndGetMetadata(
                    templateBucket, templateKey,
                    location.getBucket(), location.getObjectKey()
            );

            contentType = templateMetadata.getContentType();
            imageSize = templateMetadata.getSize();

            String publicUrl = buildPublicUrl(publicBaseUrl, location.getObjectKey());

            Contest contest = modelMapper.map(request, Contest.class);
            contest.setId(null);
            contest.setOrganizerId(organizerId);
            contest.setContentVerified(false);
            contest.setOpen(true);
            contest.setStatus(ContestStatus.CREATED);

            contest.setCoverImage(new Contest.CoverImageRef(
                    location.getObjectKey(),
                    publicUrl,
                    contentType,
                    imageSize
            ));

            List<Stage> stages = new ArrayList<>();
            for (StageRequest stageDto : request.getStages()) {
                Stage stageEntity = mapStageRequestToEntity(stageDto);
                stageEntity.setContest(contest); // Ustawiamy relację zwrotną
                stages.add(stageEntity);
            }
            contest.setStages(stages);

            Contest saved = contestRepository.save(contest);

            CreateContestResponse response = new CreateContestResponse(saved.getId());
            return ActionResult.success(response);

        } catch (Exception ex) {
            logger.error("Failed to create contest for organizer {}: {}", organizerId, ex.getMessage(), ex);

            if (location != null) {
                try {
                    minioService.delete(location.getBucket(), location.getObjectKey());
                } catch (Exception e) {
                    logger.error("COMPENSATION FAILED: Could not delete MinIO object {}", location.getObjectKey(), e);
                }
            }
            return ActionResult.failure(ProblemDetail.forStatusAndDetail(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create contest."));
        }
    }

    private Stage mapStageRequestToEntity(StageRequest dto) {
        return switch (dto.getType()) {
            case JURY_VOTING -> modelMapper.map(dto, JuryVoteStage.class);
            case QUIZ -> modelMapper.map(dto, QuizStage.class);
            case SURVEY -> modelMapper.map(dto, SurveyStage.class);
            case PUBLIC_VOTING -> modelMapper.map(dto, PublicVoteStage.class);
            case CUSTOM -> modelMapper.map(dto, GenericStage.class);
        };
    }

    private static String buildPublicUrl(String baseUrl, String key) {
        if (baseUrl == null || baseUrl.isBlank()) {
            return null;
        }
        String cleanKey = key.startsWith("/") ? key.substring(1) : key;
        String cleanBaseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        return cleanBaseUrl + "/" + cleanKey;
    }
}