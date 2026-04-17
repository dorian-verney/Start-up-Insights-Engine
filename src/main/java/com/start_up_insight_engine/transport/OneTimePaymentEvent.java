package com.start_up_insight_engine.transport;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;

@Getter
public class OneTimePaymentEvent extends KafkaEventWrapper {

    @NotEmpty
    private String description;

    @NotNull
    private Integer amount;
}
