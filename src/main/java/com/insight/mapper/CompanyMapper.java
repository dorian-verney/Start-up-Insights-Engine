package com.insight.mapper;

import com.insight.database.entity.Company;
import com.insight.dto.CompanyResponse;
import org.springframework.stereotype.Component;

@Component
public class CompanyMapper {

    public CompanyResponse toDto(Company company) {
        return CompanyResponse.builder()
                .id(company.getId())
                .name(company.getName())
                .build();
    }
}