package com.insight.poller.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

// DTO from Mock response

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CrmSubscriptionDto {
    private String id;

    private String email;

    private String plan;

    private BigDecimal amountMonthly;

    private String status;

    private Instant createdAt;

    private Instant updatedAt;

    private Instant cancelledAt;
}