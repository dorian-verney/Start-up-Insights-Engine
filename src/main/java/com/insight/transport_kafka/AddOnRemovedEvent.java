package com.insight.transport_kafka;

import com.insight.database.enums.AddOnType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class AddOnRemovedEvent extends KafkaEventWrapper {
    @NotNull
    private Long id;

    @NotNull
    private AddOnType addOnType;

    @NotNull
    private BigDecimal mrrAmount;
}
