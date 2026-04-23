package com.insight.mapper;

import com.insight.database.entity.MrrSnapshot;
import com.insight.dto.MrrResponse;

import org.springframework.stereotype.Component;

@Component
public class MrrMapper {
    public MrrResponse toDto(MrrSnapshot mrr) {
        return MrrResponse.builder()
                .timestamp(mrr.getTimestamp())
                .amount(mrr.getAmount())
                .delta(mrr.getDelta())
                .reason(mrr.getReason())
                .company(mrr.getCompany())
                .build();
    }
}
