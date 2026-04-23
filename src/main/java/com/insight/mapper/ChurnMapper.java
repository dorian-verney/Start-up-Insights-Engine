package com.insight.mapper;

import com.insight.database.entity.ChurnSnapshot;
import com.insight.dto.ChurnResponse;
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
