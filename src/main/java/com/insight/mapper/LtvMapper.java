package com.insight.mapper;

import com.insight.database.entity.LtvSnapshot;
import com.insight.dto.LtvResponse;
import org.springframework.stereotype.Component;

@Component
public class LtvMapper {
    public LtvResponse toDto(LtvSnapshot ltv) {
        return LtvResponse.builder()
                .timestamp(ltv.getTimestamp())
                .amountTheoric(ltv.getAmountTheoric())
                .amountReal(ltv.getAmountReal())
                .reason(ltv.getReason())
                .company(ltv.getCompany())
                .build();
    }
}
