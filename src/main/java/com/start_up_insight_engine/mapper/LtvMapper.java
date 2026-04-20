package com.start_up_insight_engine.mapper;

import com.start_up_insight_engine.database.entity.ChurnSnapshot;
import com.start_up_insight_engine.database.entity.LtvSnapshot;
import com.start_up_insight_engine.dto.ChurnResponse;
import com.start_up_insight_engine.dto.LtvResponse;
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
