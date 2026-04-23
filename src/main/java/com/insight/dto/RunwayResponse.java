package com.insight.dto;

import com.insight.database.entity.Company;
import com.insight.database.enums.Trigger;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Getter
public class RunwayResponse {

    private LocalDateTime timestamp;

    private BigDecimal liquidity;

    private BigDecimal totalCost;

    private Double runway;

    private Trigger reason;

    private Company company;
}
