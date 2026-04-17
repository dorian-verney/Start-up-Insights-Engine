package com.start_up_insight_engine.controller;


import com.start_up_insight_engine.database.entity.ChurnSnapshot;
import com.start_up_insight_engine.database.entity.LtvSnapshot;
import com.start_up_insight_engine.database.entity.MrrSnapshot;
import com.start_up_insight_engine.database.entity.RunwaySnapshot;
import com.start_up_insight_engine.dto.ChurnResponse;
import com.start_up_insight_engine.dto.LtvResponse;
import com.start_up_insight_engine.dto.MrrResponse;
import com.start_up_insight_engine.dto.RunwayResponse;
import com.start_up_insight_engine.mapper.ChurnMapper;
import com.start_up_insight_engine.mapper.LtvMapper;
import com.start_up_insight_engine.mapper.MrrMapper;
import com.start_up_insight_engine.mapper.RunwayMapper;
import com.start_up_insight_engine.service.ChurnService;
import com.start_up_insight_engine.service.LtvService;
import com.start_up_insight_engine.service.MrrService;

import com.start_up_insight_engine.service.RunwayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/metrics")
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


    // -----------------------------------------------
    // MRR MAPPING
    // -----------------------------------------------
    @GetMapping("/mrr")
    public ResponseEntity<MrrResponse> getLastOneMrr() {
        MrrSnapshot snap = mrrService.findLastOne()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "MRR not found"));
        return ResponseEntity.ok(mrrMapper.toDto(snap));
    }

    @GetMapping("/mrr/history")
    public ResponseEntity<List<MrrResponse>> getAllMrr() {
        List<MrrSnapshot> snaps = mrrService.findAll();
        return ResponseEntity.ok(snaps.stream().map(mrrMapper::toDto).collect(toList()));
    }

    @GetMapping("/mrr/before/{year}/{month}")
    public ResponseEntity<List<MrrResponse>> getToMonthMrr(
            @PathVariable int year, @PathVariable Month month
    ) {
        List<MrrSnapshot> snaps = mrrService.findToMonth(YearMonth.of(year, month)
                        .atDay(1)
                        .atStartOfDay());
        return ResponseEntity.ok(snaps.stream().map(mrrMapper::toDto).collect(toList()));
    }

    @GetMapping("/mrr/after/{year}/{month}")
    public ResponseEntity<List<MrrResponse>> getFromMonthMrr(
            @PathVariable int year, @PathVariable Month month
    ) {
        List<MrrSnapshot> snaps = mrrService.findFromMonth(YearMonth.of(year, month)
                        .atDay(1)
                        .atStartOfDay());
        return ResponseEntity.ok(snaps.stream().map(mrrMapper::toDto).collect(toList()));
    }


    // -----------------------------------------------
    // CHURN MAPPING
    // -----------------------------------------------
    @GetMapping("/churn")
    public ResponseEntity<ChurnResponse> getLastOneChurn() {
        ChurnSnapshot snap = churnService.findLastOne()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Churn not found"));
        return ResponseEntity.ok(churnMapper.toDto(snap));
    }

    @GetMapping("/churn/history")
    public ResponseEntity<List<ChurnResponse>> getAllChurn() {
        List<ChurnSnapshot> snaps = churnService.findAll();
        return ResponseEntity.ok(snaps.stream().map(churnMapper::toDto).collect(toList()));
    }

    @GetMapping("/churn/before/{year}/{month}")
    public ResponseEntity<List<ChurnResponse>> getToMonthChurn(
            @PathVariable int year, @PathVariable Month month
    ) {
        List<ChurnSnapshot> snaps = churnService.findToMonth(YearMonth.of(year, month)
                .atDay(1)
                .atStartOfDay());
        return ResponseEntity.ok(snaps.stream().map(churnMapper::toDto).collect(toList()));
    }

    @GetMapping("/churn/after/{year}/{month}")
    public ResponseEntity<List<ChurnResponse>> getFromMonthChurn(
            @PathVariable int year, @PathVariable Month month
    ) {
        List<ChurnSnapshot> snaps = churnService.findFromMonth(YearMonth.of(year, month)
                .atDay(1)
                .atStartOfDay());
        return ResponseEntity.ok(snaps.stream().map(churnMapper::toDto).collect(toList()));
    }


    // -----------------------------------------------
    // LTV MAPPING
    // -----------------------------------------------
    @GetMapping("/ltv")
    public ResponseEntity<LtvResponse> getLastOneLtv() {
        LtvSnapshot snap = ltvService.findLastOne()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "LTV not found"));
        return ResponseEntity.ok(ltvMapper.toDto(snap));
    }

    @GetMapping("/ltv/history")
    public ResponseEntity<List<LtvResponse>> getAllLtv() {
        List<LtvSnapshot> snaps = ltvService.findAll();
        return ResponseEntity.ok(snaps.stream().map(ltvMapper::toDto).collect(toList()));
    }

    @GetMapping("/ltv/before/{year}/{month}")
    public ResponseEntity<List<LtvResponse>> getToMonthLtv(
            @PathVariable int year, @PathVariable Month month
    ) {
        List<LtvSnapshot> snaps = ltvService.findToMonth(YearMonth.of(year, month)
                .atDay(1)
                .atStartOfDay());
        return ResponseEntity.ok(snaps.stream().map(ltvMapper::toDto).collect(toList()));
    }

    @GetMapping("/ltv/after/{year}/{month}")
    public ResponseEntity<List<LtvResponse>> getFromMonthLtv(
            @PathVariable int year, @PathVariable Month month
    ) {
        List<LtvSnapshot> snaps = ltvService.findFromMonth(YearMonth.of(year, month)
                .atDay(1)
                .atStartOfDay());
        return ResponseEntity.ok(snaps.stream().map(ltvMapper::toDto).collect(toList()));
    }


    // -----------------------------------------------
    // RUNWAY MAPPING
    // -----------------------------------------------
    @GetMapping("/runway")
    public ResponseEntity<RunwayResponse> getLastOneRunway() {
        RunwaySnapshot snap = runwayService.findLastOne()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Runway not found"));
        return ResponseEntity.ok(runwayMapper.toDto(snap));
    }

    @GetMapping("/runway/history")
    public ResponseEntity<List<RunwayResponse>> getAllRunway() {
        List<RunwaySnapshot> snaps = runwayService.findAll();
        return ResponseEntity.ok(snaps.stream().map(runwayMapper::toDto).collect(toList()));
    }

    @GetMapping("/runway/before/{year}/{month}")
    public ResponseEntity<List<RunwayResponse>> getToMonthRunway(
            @PathVariable int year, @PathVariable Month month
    ) {
        List<RunwaySnapshot> snaps = runwayService.findToMonth(YearMonth.of(year, month)
                .atDay(1)
                .atStartOfDay());
        return ResponseEntity.ok(snaps.stream().map(runwayMapper::toDto).collect(toList()));
    }

    @GetMapping("/runway/after/{year}/{month}")
    public ResponseEntity<List<RunwayResponse>> getFromMonthRunway(
            @PathVariable int year, @PathVariable Month month
    ) {
        List<RunwaySnapshot> snaps = runwayService.findFromMonth(YearMonth.of(year, month)
                .atDay(1)
                .atStartOfDay());
        return ResponseEntity.ok(snaps.stream().map(runwayMapper::toDto).collect(toList()));
    }
}