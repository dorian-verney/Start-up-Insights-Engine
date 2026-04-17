package com.start_up_insight_engine.mapper;

import com.start_up_insight_engine.database.entity.RunwaySnapshot;
import com.start_up_insight_engine.dto.RunwayResponse;

import org.springframework.stereotype.Component;

@Component   // to inject mapper inside controller
public class RunwayMapper {
    public RunwayResponse toDto(RunwaySnapshot runway) {
        return RunwayResponse.builder()
                .timestamp(runway.getTimestamp())
                .liquidity(runway.getLiquidity())
                .totalCost(runway.getTotalCost())
                .runway(runway.getRunway())
                .reason(runway.getReason())
                .build();

    }
}
