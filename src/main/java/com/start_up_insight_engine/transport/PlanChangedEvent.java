package com.start_up_insight_engine.transport;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class PlanChangedEvent extends KafkaEventWrapper {

    @NotNull
    private Long oldPlanId;

    @NotNull
    private Long newPlanId;

    @NotNull
    private BigDecimal oldMrrAmount;

    @NotNull
    private BigDecimal newMrrAmount;
}
