package com.start_up_insight_engine.transport;

import com.start_up_insight_engine.database.enums.PlanType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class SubscriptionStartedEvent extends KafkaEventWrapper {

    @NotBlank
    private String subscriberName;

    @NotBlank
    private String subscriberEmail;

    @NotNull
    private PlanType plantype;

    @NotNull
    private BigDecimal mrrAmount;
}
