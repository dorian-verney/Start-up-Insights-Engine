package com.start_up_insight_engine.transport;

import com.start_up_insight_engine.database.enums.AddOnType;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class AddOnAddedEvent extends KafkaEventWrapper {

    @NotNull
    private Long id;

    @NotNull
    private AddOnType addOnType;

    @NotNull
    private BigDecimal mrrAmount;
}
