package com.start_up_insight_engine.transport_kafka;

import com.start_up_insight_engine.database.entity.Plan;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class PlanChangedEvent extends KafkaEventWrapper {

    @NotNull
    private Plan oldPlan;

    @NotNull
    private Plan newPlan;

    @NotNull
    private BigDecimal oldMrrAmount;

    @NotNull
    private BigDecimal newMrrAmount;
}
