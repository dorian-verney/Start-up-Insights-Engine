package com.insight.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CompanyResponse {

    private Long id;

    private String name;
}
