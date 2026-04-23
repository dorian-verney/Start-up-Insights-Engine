package com.insight.controller;


import com.insight.database.entity.ChurnSnapshot;
import com.insight.database.entity.Company;
import com.insight.database.entity.LtvSnapshot;
import com.insight.database.entity.MrrSnapshot;
import com.insight.database.entity.RunwaySnapshot;
import com.insight.dto.ChurnResponse;
import com.insight.dto.LtvResponse;
import com.insight.dto.MrrResponse;
import com.insight.dto.RunwayResponse;
import com.insight.mapper.ChurnMapper;
import com.insight.mapper.LtvMapper;
import com.insight.mapper.MrrMapper;
import com.insight.mapper.RunwayMapper;
import com.insight.service.ChurnService;
import com.insight.service.CompanyService;
import com.insight.service.LtvService;
import com.insight.service.MrrService;
import com.insight.service.RunwayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.YearMonth;
import java.util.List;

import static java.util.stream.Collectors.toList;

@CrossOrigin(origins = {
        "http://localhost:5173"
//        "https://ton-frontend.vercel.app"
})
@RestController
@RequestMapping("/api/metrics/{companyId}")
public class MetricsController {

    @Autowired
    MrrService mrrService;
    @Autowired
    MrrMapper mrrMapper;

    @Autowired
    ChurnService churnService;
    @Autowired
    ChurnMapper churnMapper;

    @Autowired
    LtvService ltvService;
    @Autowired
    LtvMapper ltvMapper;

    @Autowired
    RunwayService runwayService;
    @Autowired
    RunwayMapper runwayMapper;

    @Autowired
    CompanyService companyService;

    private Company findById(Long companyId) {
        return companyService.findById(companyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found"));
    }

    // -----------------------------------------------
    // MRR MAPPING
    // -----------------------------------------------
    @GetMapping("/mrr")
    public ResponseEntity<MrrResponse> getLastOneMrr(@PathVariable Long companyId) {
        Company company = findById(companyId);
        MrrSnapshot snap = mrrService.findLastOne(company)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "MRR not found"));
        return ResponseEntity.ok(mrrMapper.toDto(snap));
    }

    @GetMapping("/mrr/history")
    public ResponseEntity<List<MrrResponse>> getAllMrr(@PathVariable Long companyId) {
        Company company = findById(companyId);
        List<MrrSnapshot> snaps = mrrService.findAll(company);
        return ResponseEntity.ok(snaps.stream().map(mrrMapper::toDto).collect(toList()));
    }

    @GetMapping("/mrr/before/{year}/{month}")
    public ResponseEntity<List<MrrResponse>> getToMonthMrr(
            @PathVariable Long companyId, @PathVariable int year, @PathVariable int month
    ) {
        Company company = findById(companyId);
        List<MrrSnapshot> snaps = mrrService.findToMonth(company, YearMonth.of(year, month)
                        .atDay(1)
                        .atStartOfDay());
        return ResponseEntity.ok(snaps.stream().map(mrrMapper::toDto).collect(toList()));
    }

    @GetMapping("/mrr/after/{year}/{month}")
    public ResponseEntity<List<MrrResponse>> getFromMonthMrr(
            @PathVariable Long companyId, @PathVariable int year, @PathVariable int month
    ) {
        Company company = findById(companyId);
        List<MrrSnapshot> snaps = mrrService.findFromMonth(company, YearMonth.of(year, month)
                        .atDay(1)
                        .atStartOfDay());
        return ResponseEntity.ok(snaps.stream().map(mrrMapper::toDto).collect(toList()));
    }


    // -----------------------------------------------
    // CHURN MAPPING
    // -----------------------------------------------
    @GetMapping("/churn")
    public ResponseEntity<ChurnResponse> getLastOneChurn(@PathVariable Long companyId) {
        Company company = findById(companyId);
        ChurnSnapshot snap = churnService.findLastOne(company)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Churn not found"));
        return ResponseEntity.ok(churnMapper.toDto(snap));
    }

    @GetMapping("/churn/history")
    public ResponseEntity<List<ChurnResponse>> getAllChurn(@PathVariable Long companyId) {
        Company company = findById(companyId);
        List<ChurnSnapshot> snaps = churnService.findAll(company);
        return ResponseEntity.ok(snaps.stream().map(churnMapper::toDto).collect(toList()));
    }

    @GetMapping("/churn/before/{year}/{month}")
    public ResponseEntity<List<ChurnResponse>> getToMonthChurn(
            @PathVariable Long companyId, @PathVariable int year, @PathVariable int month
    ) {
        Company company = findById(companyId);
        List<ChurnSnapshot> snaps = churnService.findToMonth(company, YearMonth.of(year, month)
                .atDay(1)
                .atStartOfDay());
        return ResponseEntity.ok(snaps.stream().map(churnMapper::toDto).collect(toList()));
    }

    @GetMapping("/churn/after/{year}/{month}")
    public ResponseEntity<List<ChurnResponse>> getFromMonthChurn(
            @PathVariable Long companyId, @PathVariable int year, @PathVariable int month
    ) {
        Company company = findById(companyId);
        List<ChurnSnapshot> snaps = churnService.findFromMonth(company, YearMonth.of(year, month)
                .atDay(1)
                .atStartOfDay());
        return ResponseEntity.ok(snaps.stream().map(churnMapper::toDto).collect(toList()));
    }


    // -----------------------------------------------
    // LTV MAPPING
    // -----------------------------------------------
    @GetMapping("/ltv")
    public ResponseEntity<LtvResponse> getLastOneLtv(@PathVariable Long companyId) {
        Company company = findById(companyId);
        LtvSnapshot snap = ltvService.findLastOne(company)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "LTV not found"));
        return ResponseEntity.ok(ltvMapper.toDto(snap));
    }

    @GetMapping("/ltv/history")
    public ResponseEntity<List<LtvResponse>> getAllLtv(@PathVariable Long companyId) {
        Company company = findById(companyId);
        List<LtvSnapshot> snaps = ltvService.findAll(company);
        return ResponseEntity.ok(snaps.stream().map(ltvMapper::toDto).collect(toList()));
    }

    @GetMapping("/ltv/before/{year}/{month}")
    public ResponseEntity<List<LtvResponse>> getToMonthLtv(
            @PathVariable Long companyId, @PathVariable int year, @PathVariable int month
    ) {
        Company company = findById(companyId);
        List<LtvSnapshot> snaps = ltvService.findToMonth(company, YearMonth.of(year, month)
                .atDay(1)
                .atStartOfDay());
        return ResponseEntity.ok(snaps.stream().map(ltvMapper::toDto).collect(toList()));
    }

    @GetMapping("/ltv/after/{year}/{month}")
    public ResponseEntity<List<LtvResponse>> getFromMonthLtv(
            @PathVariable Long companyId, @PathVariable int year, @PathVariable int month
    ) {
        Company company = findById(companyId);
        List<LtvSnapshot> snaps = ltvService.findFromMonth(company, YearMonth.of(year, month)
                .atDay(1)
                .atStartOfDay());
        return ResponseEntity.ok(snaps.stream().map(ltvMapper::toDto).collect(toList()));
    }


    // -----------------------------------------------
    // RUNWAY MAPPING
    // -----------------------------------------------
    @GetMapping("/runway")
    public ResponseEntity<RunwayResponse> getLastOneRunway(@PathVariable Long companyId) {
        Company company = findById(companyId);
        RunwaySnapshot snap = runwayService.findLastOne(company)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Runway not found"));
        return ResponseEntity.ok(runwayMapper.toDto(snap));
    }

    @GetMapping("/runway/history")
    public ResponseEntity<List<RunwayResponse>> getAllRunway(@PathVariable Long companyId) {
        Company company = findById(companyId);
        List<RunwaySnapshot> snaps = runwayService.findAll(company);
        return ResponseEntity.ok(snaps.stream().map(runwayMapper::toDto).collect(toList()));
    }

    @GetMapping("/runway/before/{year}/{month}")
    public ResponseEntity<List<RunwayResponse>> getToMonthRunway(
            @PathVariable Long companyId, @PathVariable int year, @PathVariable int month
    ) {
        Company company = findById(companyId);
        List<RunwaySnapshot> snaps = runwayService.findToMonth(company, YearMonth.of(year, month)
                .atDay(1)
                .atStartOfDay());
        return ResponseEntity.ok(snaps.stream().map(runwayMapper::toDto).collect(toList()));
    }

    @GetMapping("/runway/after/{year}/{month}")
    public ResponseEntity<List<RunwayResponse>> getFromMonthRunway(
            @PathVariable Long companyId, @PathVariable int year, @PathVariable int month
    ) {
        Company company = findById(companyId);
        List<RunwaySnapshot> snaps = runwayService.findFromMonth(company, YearMonth.of(year, month)
                .atDay(1)
                .atStartOfDay());
        return ResponseEntity.ok(snaps.stream().map(runwayMapper::toDto).collect(toList()));
    }
}
