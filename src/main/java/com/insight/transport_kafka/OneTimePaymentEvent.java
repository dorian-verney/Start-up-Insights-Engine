package com.insight.transport_kafka;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class OneTimePaymentEvent extends KafkaEventWrapper {

    @NotEmpty
    private String description;

    @NotNull
    private Integer amount;
}
