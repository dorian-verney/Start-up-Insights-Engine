package com.insight.dto;

import com.insight.database.entity.Company;
import com.insight.database.enums.Trigger;
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

    private Company company;
}
