package com.start_up_insight_engine.controller;


import com.start_up_insight_engine.database.entity.Company;
import com.start_up_insight_engine.dto.CompanyResponse;
import com.start_up_insight_engine.mapper.CompanyMapper;
import com.start_up_insight_engine.mapper.MrrMapper;
import com.start_up_insight_engine.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static java.util.stream.Collectors.toList;

@CrossOrigin(origins = {
        "http://localhost:5173"
//        "https://ton-frontend.vercel.app"
})
@RestController
@RequestMapping("/api/companies/")
public class CompanyController {

    @Autowired
    CompanyService companyService;

    @Autowired
    CompanyMapper companyMapper;

    @GetMapping("/{companyId}")
    public ResponseEntity<CompanyResponse> findById(@PathVariable Long companyId) {
        Company company = companyService.findById(companyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found"));
        return ResponseEntity.ok(companyMapper.toDto(company));
    }

    @GetMapping
    public ResponseEntity<List<CompanyResponse>> findAll() {
        List<Company> companies = companyService.findAll();
        return ResponseEntity.ok(companies.stream().map(companyMapper::toDto).collect(toList()));
    }
}
