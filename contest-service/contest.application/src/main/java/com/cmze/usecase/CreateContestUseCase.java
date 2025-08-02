package com.cmze.usecase;

import com.cmze.entity.Contest;
import com.cmze.entity.User;
import com.cmze.repository.ContestRepository;
import com.cmze.request.CreateContestRequest;
import com.cmze.response.CreateContestResponse;
import com.cmze.shared.ActionResult;
import com.cmze.shared.SocialsService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

@UseCase
public class CreateContestUseCase {

    private final ContestRepository contestRepository;
    private final SocialsService socialMediaService;
    private final ModelMapper modelMapper;

    @Autowired
    public CreateContestUseCase(ContestRepository contestRepository,
                                SocialsService socialMediaService,
                                ModelMapper modelMapper) {
        this.contestRepository = contestRepository;
        this.socialMediaService = socialMediaService;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public ActionResult<CreateContestResponse> execute(CreateContestRequest request, String username) {
        try {
            if (!StringUtils.hasText(request.getName()) ||
                    request.getStartDate() == null ||
                    request.getEndDate() == null ||
                    request.getStartDate().isAfter(request.getEndDate())) {
                ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                        HttpStatus.BAD_REQUEST, "Invalid contest data: name and dates are required and must be valid."
                );
                return ActionResult.failure(problem);
            }

            User organizer = userService.findByUsername(username);
            if (organizer == null) {
                ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                        HttpStatus.NOT_FOUND, "Organizer account not found."
                );
                return ActionResult.failure(problem);
            }

            boolean publicVoting = request.isPublicVoting() && organizer.getFollowerCount() >= 100;

            Contest contest = modelMapper.map(request, Contest.class);
            contest.setId(UUID.randomUUID());
            contest.setOrganizer(organizer);
            contest.setPublicVoting(publicVoting);

            contestRepository.save(contest);

            if (contest.isPublishToSocialMedia()) {
                socialMediaService.publishContestAnnouncement(contest, organizer);
            }

            CreateContestResponse response = new CreateContestResponse(
                    contest.getId(),
                    contest.getName(),
                    contest.getStartDate(),
                    contest.getEndDate(),
                    contest.getLocation()
            );

            return ActionResult.success(response);

        } catch (Exception ex) {
            ProblemDetail error = ProblemDetail.forStatusAndDetail(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to create contest: " + ex.getMessage()
            );
            return ActionResult.failure(error);
        }
    }
}
