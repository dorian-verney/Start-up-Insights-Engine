package com.start_up_insight_engine.mapper;

import com.start_up_insight_engine.database.entity.ChurnSnapshot;
import com.start_up_insight_engine.database.entity.MrrSnapshot;
import com.start_up_insight_engine.dto.ChurnResponse;
import com.start_up_insight_engine.dto.MrrResponse;
import org.springframework.stereotype.Component;

@Component
public class ChurnMapper {
    public ChurnResponse toDto(ChurnSnapshot churn) {
        return ChurnResponse.builder()
                .timestamp(churn.getTimestamp())
                .rate(churn.getRate())
                .activeSubscribers(churn.getActiveSubscribers())
                .reason(churn.getReason())
                .company(churn.getCompany())
                .build();
    }
}
