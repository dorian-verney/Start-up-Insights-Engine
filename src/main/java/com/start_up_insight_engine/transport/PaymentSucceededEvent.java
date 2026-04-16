package com.start_up_insight_engine.transport;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Month;

public class PaymentSucceededEvent extends KafkaEventWrapper {

    @NotNull
    private Month month;

    @NotNull
    private BigDecimal amount;
}
