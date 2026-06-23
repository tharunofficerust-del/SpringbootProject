package com.tharun.vessel_risk.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tharun.vessel_risk.dto.CreateDelayReportRequest;
import com.tharun.vessel_risk.dto.DelayResponse;
import com.tharun.vessel_risk.service.DelayService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/delays")
@RequiredArgsConstructor
public class DelayController {

    private final DelayService delayService;

    @PostMapping
    public ResponseEntity<DelayResponse>
    createDelayReport(
            @RequestBody CreateDelayReportRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(delayService.createDelayReport(request));
    }

    @GetMapping("/vessel/{voyageNumber}")
    public ResponseEntity<List<DelayResponse>>
    getDelayReports(
            @PathVariable String voyageNumber) {

        return ResponseEntity.ok(
                delayService.getDelayReports(voyageNumber));
    }
}