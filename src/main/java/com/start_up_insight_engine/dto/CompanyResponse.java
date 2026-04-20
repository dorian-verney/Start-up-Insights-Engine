package com.start_up_insight_engine.dto;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CompanyResponse {

    private Long id;

    private String name;
}
