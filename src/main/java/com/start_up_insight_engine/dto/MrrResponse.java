package com.start_up_insight_engine.dto;

import com.start_up_insight_engine.database.entity.Company;
import com.start_up_insight_engine.database.enums.Trigger;
import lombok.Builder;
import lombok.Getter;


import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Getter
public class MrrResponse {

    private LocalDateTime timestamp;

    private BigDecimal amount; // mrr total qui s'ajoute

    private BigDecimal delta;  // variation avec new action

    private Trigger reason;

    private Company company;
}
