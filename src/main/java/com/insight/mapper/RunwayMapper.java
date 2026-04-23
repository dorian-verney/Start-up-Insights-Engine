package com.insight.mapper;

import com.insight.database.entity.RunwaySnapshot;
import com.insight.dto.RunwayResponse;

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
                .company(runway.getCompany())
                .build();

    }
}
