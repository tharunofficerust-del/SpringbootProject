package com.tharun.vessel_risk.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tharun.vessel_risk.dto.DashboardSummaryResponse;
import com.tharun.vessel_risk.entity.Shipment;
import com.tharun.vessel_risk.entity.VesselSchedule;
import com.tharun.vessel_risk.service.DashboardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryResponse>
            getSummary() {

        return ResponseEntity.ok(
                dashboardService.getSummary());
    }

    @GetMapping("/high-risk-vessels")
    public ResponseEntity<List<VesselSchedule>>
            getHighRiskVessels() {

        return ResponseEntity.ok(
                dashboardService
                        .getHighRiskVessels());
    }

    @GetMapping("/at-risk-shipments")
    public ResponseEntity<List<Shipment>>
            getAtRiskShipments() {

        return ResponseEntity.ok(
                dashboardService
                        .getAtRiskShipments());
    }
}