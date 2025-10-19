package com.cmze.internal.service.stagesettings.strategy.impl;

import com.cmze.entity.Stage;
import com.cmze.entity.stagesettings.StageJuryConfig;
import com.cmze.enums.JuryRevealMode;
import com.cmze.enums.StageType;
import com.cmze.internal.service.stagesettings.strategy.StageSettingsStrategy;
import com.cmze.repository.StageJuryConfigRepository;
import com.cmze.request.CreateContestRequest;
import com.cmze.request.stagesettings.JuryVotingSettingsRequest;
import com.cmze.response.stagesettings.JuryVotingSettingsResponse;
import com.cmze.response.stagesettings.StageSettingsResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;

@Component
public class JuryStageSettingsStrategy implements StageSettingsStrategy {

    private final StageJuryConfigRepository juryRepo;

    public JuryStageSettingsStrategy(StageJuryConfigRepository juryRepo) {
        this.juryRepo = juryRepo;
    }

    @Override public StageType type() { return StageType.JURY_VOTING; }

    @Override
    public ProblemDetail validate(CreateContestRequest.StageRequest dto) {
        var st = dto.getSettings();
        if (!(st instanceof JuryVotingSettingsRequest js)) {
            return ProblemDetail.forStatusAndDetail(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "settings must be JuryVotingSettings for JURY_VOTING"
            );
        }
        if (js.getWeight() != null && js.getWeight() <= 0.0) {
            return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, "weight must be > 0");
        }
        if (js.getMaxScore() != null && js.getMaxScore() < 1) {
            return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, "maxScore must be >= 1");
        }
        if (js.getJuryRevealMode() == null) {
            return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, "juryRevealMode is required");
        }
        return null;
    }

    @Override
    public void apply(CreateContestRequest.StageRequest dto, Stage stage) {
        var js = (JuryVotingSettingsRequest) dto.getSettings();

        double weight = js.getWeight() != null ? js.getWeight() : 1.0;
        int max       = js.getMaxScore() != null ? js.getMaxScore() : 10;
        JuryRevealMode mode = js.getJuryRevealMode() != null ? js.getJuryRevealMode() : JuryRevealMode.IMMEDIATE;
        boolean show  = js.getShowJudgeNames() == null || js.getShowJudgeNames();

        var cfg = new StageJuryConfig();
        cfg.setStage(stage);     // @MapsId
        cfg.setWeight(weight);
        cfg.setMaxScore(max);
        cfg.setJuryRevealMode(mode);
        cfg.setShowJudgeNames(show);
        juryRepo.save(cfg);
    }

    @Override
    public StageSettingsResponse runStage(long stageId) {
        var cfg = repo.findById(stageId).orElseThrow(
                () -> new IllegalStateException("Missing JURY_VOTING config for stageId=" + stageId)
        );
        JuryVotingSettingsResponse r = new JuryVotingSettingsResponse();
        r.setWeight(cfg.getWeight() <= 0 ? 1.0 : cfg.getWeight());
        r.setMaxScore(cfg.getMaxScore() <= 0 ? 10 : cfg.getMaxScore());
        r.setJuryRevealMode(cfg.getJuryRevealMode().name());
        r.setShowJudgeNames(cfg.isShowJudgeNames());
        return r;
    }
}
