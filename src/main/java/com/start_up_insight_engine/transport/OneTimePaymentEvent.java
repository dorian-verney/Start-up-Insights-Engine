package com.start_up_insight_engine.transport;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;

public class OneTimePaymentEvent extends KafkaEventWrapper {

    @NotEmpty
    private String description;

    @NotNull
    private BigDecimal amount;
}
