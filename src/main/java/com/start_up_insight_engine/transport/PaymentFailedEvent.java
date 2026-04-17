package com.start_up_insight_engine.transport;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Month;

@Getter
public class PaymentFailedEvent extends KafkaEventWrapper {

    @NotEmpty
    private String reason;

    @NotNull
    private Integer attemptNumber;

    @NotNull
    private Month month;

    @NotNull
    private BigDecimal amount;
}
