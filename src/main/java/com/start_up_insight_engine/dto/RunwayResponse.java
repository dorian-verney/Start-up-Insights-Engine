package com.start_up_insight_engine.dto;

import com.start_up_insight_engine.database.enums.Trigger;
import jakarta.persistence.Column;
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
}
