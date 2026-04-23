package com.insight.dto;

import com.insight.database.entity.Company;
import com.insight.database.enums.Trigger;
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
