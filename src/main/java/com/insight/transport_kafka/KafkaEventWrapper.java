package com.insight.transport_kafka;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
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
