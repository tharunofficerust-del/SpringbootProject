package com.tharun.vessel_risk.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tharun.vessel_risk.dto.CreateDelayReportRequest;
import com.tharun.vessel_risk.dto.DelayResponse;
import com.tharun.vessel_risk.dto.UpdateDelayRequest;
import com.tharun.vessel_risk.service.DelayService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Valid
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

    @DeleteMapping("/{delayId}")
    public ResponseEntity<Void>
    deleteDelayReport(
            @PathVariable Long delayId) {

        delayService.deleteDelayReport(
                delayId);

        return ResponseEntity
                .noContent()
                .build();
    }
    
    @PutMapping("/{delayId}")
        public ResponseEntity<DelayResponse> updateDelayReport(
                @PathVariable Long delayId,
                @Valid @RequestBody UpdateDelayRequest request) {

        DelayResponse response =
                delayService.updateDelayReport(
                        delayId,
                        request);

        return ResponseEntity.ok(response);
        }

     @GetMapping
        public ResponseEntity<List<DelayResponse>>
        getAllDelayReports() {

        return ResponseEntity.ok(
                delayService
                        .getAllDelayReports()
        );
        }
}