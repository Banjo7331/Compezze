package com.cmze.internal.service.stagesettings.strategy.impl;

import com.cmze.entity.Stage;
import com.cmze.entity.stagesettings.StagePublicConfig;
import com.cmze.enums.StageType;
import com.cmze.internal.service.stagesettings.strategy.StageSettingsStrategy;
import com.cmze.repository.StagePublicConfigRepository;
import com.cmze.request.CreateContestRequest;
import com.cmze.request.stagesettings.PublicVotingSettingsRequest;
import com.cmze.response.stagesettings.PublicVotingSettingsResponse;
import com.cmze.response.stagesettings.StageSettingsResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class PublicStageSettingsStrategy implements StageSettingsStrategy {

    private final StagePublicConfigRepository publicRepo;

    public PublicStageSettingsStrategy(StagePublicConfigRepository publicRepo) {
        this.publicRepo = publicRepo;
    }

    @Override public StageType type() { return StageType.PUBLIC_VOTING; }

    @Override
    public ProblemDetail validate(CreateContestRequest.StageRequest dto) {
        var st = dto.getSettings();
        if (st == null) return null; // dopuszczamy brak — użyjemy defaultów
        if (!(st instanceof PublicVotingSettingsRequest ps)) {
            return ProblemDetail.forStatusAndDetail(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "settings must be PublicVotingSettings for PUBLIC_VOTING"
            );
        }
        if (ps.getWeight() != null && ps.getWeight() <= 0.0) {
            return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, "weight must be > 0");
        }
        return null;
    }

    @Override
    public void apply(CreateContestRequest.StageRequest dto, Stage stage) {
        double weight = 1.0;
        var st = dto.getSettings();
        if (st instanceof PublicVotingSettingsRequest ps && ps.getWeight() != null) {
            weight = ps.getWeight();
        }
        var cfg = new StagePublicConfig();
        cfg.setStage(stage);       // @MapsId → użyje stage.id
        cfg.setWeight(weight);
        cfg.setMaxScore(1);        // kosmetycznie, logicznie ignorowane dla PUBLIC
        publicRepo.save(cfg);
    }

    @Override
    public StageSettingsResponse runStage(long stageId) {
        var cfg = publicRepo.findById(stageId).orElseThrow(
                () -> new IllegalStateException("Missing PUBLIC_VOTING config for stageId=" + stageId)
        );
        var r = new PublicVotingSettingsResponse();
        r.setWeight(cfg.getWeight() <= 0 ? 1.0 : cfg.getWeight());
        r.setMaxScore(cfg.getMaxScore() <= 0 ? 1 : cfg.getMaxScore());
        return r;
    }
}
