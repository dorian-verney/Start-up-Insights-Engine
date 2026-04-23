package com.insight.transport_kafka;

import com.insight.database.entity.Plan;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Getter
@SuperBuilder
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
