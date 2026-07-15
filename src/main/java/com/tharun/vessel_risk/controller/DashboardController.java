package com.tharun.vessel_risk.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tharun.vessel_risk.dto.DashboardSummaryResponse;
import com.tharun.vessel_risk.entity.Shipment;
import com.tharun.vessel_risk.entity.VesselSchedule;
import com.tharun.vessel_risk.service.DashboardService;

import jakarta.validation.Valid;

import com.tharun.vessel_risk.dto.VoyageRiskSummaryResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Valid
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryResponse> //dto
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

    @GetMapping("/voyages/{voyageNumber}/risk-summary")
        public ResponseEntity<VoyageRiskSummaryResponse>
        getRiskSummary(

                @PathVariable String voyageNumber) {

        return ResponseEntity.ok(
                dashboardService
                        .getVoyageRiskSummary(
                                voyageNumber));
        }
}