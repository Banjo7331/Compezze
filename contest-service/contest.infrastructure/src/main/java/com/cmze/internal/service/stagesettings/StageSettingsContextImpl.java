package com.cmze.internal.service.stagesettings;

import com.cmze.entity.Stage;
import com.cmze.enums.StageType;
import com.cmze.internal.service.stagesettings.strategy.StageSettingsStrategy;
import com.cmze.request.CreateContestRequest;
import com.cmze.response.stagesettings.StageSettingsResponse;
import com.cmze.spi.StageSettingsContext;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class StageSettingsContextImpl implements StageSettingsContext {

    private final Map<StageType, StageSettingsStrategy> byType;

    public StageSettingsContextImpl(List<StageSettingsStrategy> strategies) {
        this.byType = strategies.stream()
                .collect(Collectors.toMap(StageSettingsStrategy::type, Function.identity(),
                        (a,b)->a, () -> new EnumMap<>(StageType.class)));
    }
    @Override
    public ProblemDetail validate(CreateContestRequest.StageRequest dto) {
        var s = byType.get(dto.getType());
        return (s == null) ? null : s.validate(dto);
    }

    @Override
    public void apply(CreateContestRequest.StageRequest dto, Stage stage) {
        var s = byType.get(dto.getType());
        if (s != null) s.apply(dto, stage);
    }

    @Override
    public StageSettingsResponse runStage(long stageId, StageType type) {
        var s = byType.get(type);
        if (s == null) {
            throw new IllegalStateException("No strategy for type: " + type);
        }
        return s.runStage(stageId);
    }

}
