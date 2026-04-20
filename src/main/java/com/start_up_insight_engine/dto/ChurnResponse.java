package com.start_up_insight_engine.dto;

import com.start_up_insight_engine.database.entity.Company;
import com.start_up_insight_engine.database.enums.Trigger;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class ChurnResponse {

    private LocalDateTime timestamp;

    private float rate;

    private Long activeSubscribers;

    private Trigger reason;

    private Company company;
}
