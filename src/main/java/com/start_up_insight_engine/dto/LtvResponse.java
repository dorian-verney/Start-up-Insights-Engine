package com.start_up_insight_engine.dto;

import com.start_up_insight_engine.database.enums.Trigger;
import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Getter
public class LtvResponse {

    private LocalDateTime timestamp;

    private Double amountTheoric;

    private BigDecimal amountReal;

    private Trigger reason;
}
