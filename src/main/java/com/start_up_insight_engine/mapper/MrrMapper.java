package com.start_up_insight_engine.mapper;

import com.start_up_insight_engine.database.entity.MrrSnapshot;
import com.start_up_insight_engine.dto.MrrResponse;

import org.springframework.stereotype.Component;

@Component
public class MrrMapper {
    public MrrResponse toDto(MrrSnapshot mrr) {
        return MrrResponse.builder()
                .timestamp(mrr.getTimestamp())
                .amount(mrr.getAmount())
                .delta(mrr.getDelta())
                .reason(mrr.getReason())
                .build();
    }
}
