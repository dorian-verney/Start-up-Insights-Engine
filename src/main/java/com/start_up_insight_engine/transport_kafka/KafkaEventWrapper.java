package com.start_up_insight_engine.transport_kafka;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class KafkaEventWrapper {

    @NotNull
    private UUID eventId;

    @NotBlank
    @Getter
    private String eventType;

    @NotNull
    private LocalDateTime eventTime;

    @NotNull
    private Long subscriberId;

    @NotNull
    private Long companyId;
}
