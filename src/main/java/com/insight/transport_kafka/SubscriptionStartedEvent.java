package com.insight.transport_kafka;

import com.insight.database.enums.PlanType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class SubscriptionStartedEvent extends KafkaEventWrapper {

    @NotBlank
    private String subscriberName;

    @NotBlank
    private String subscriberEmail;

    @NotNull
    private PlanType planType;

    @NotNull
    private BigDecimal mrrAmount;
}
