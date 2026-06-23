package com.tharun.vessel_risk.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tharun.vessel_risk.dto.DashboardSummaryResponse;
import com.tharun.vessel_risk.entity.Shipment;
import com.tharun.vessel_risk.entity.VesselSchedule;
import com.tharun.vessel_risk.enums.RiskLevel;
import com.tharun.vessel_risk.enums.ShipmentStatus;
import com.tharun.vessel_risk.repository.DelayReportRepository;
import com.tharun.vessel_risk.repository.ShipmentRepository;
import com.tharun.vessel_risk.repository.VesselScheduleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final VesselScheduleRepository vesselScheduleRepository;

    private final ShipmentRepository shipmentRepository;

    private final DelayReportRepository delayReportRepository;

    public DashboardSummaryResponse getSummary() {

        return DashboardSummaryResponse.builder()
                .totalVessels(
                        vesselScheduleRepository.count())
                .totalShipments(
                        shipmentRepository.count())
                .totalDelayReports(
                        delayReportRepository.count())
                .highRiskVessels(
                        vesselScheduleRepository
                                .countByRiskLevel(
                                        RiskLevel.HIGH))
                .criticalRiskVessels(
                        vesselScheduleRepository
                                .countByRiskLevel(
                                        RiskLevel.CRITICAL))
                .atRiskShipments(
                        shipmentRepository
                                .countByShipmentStatus(
                                        ShipmentStatus.AT_RISK))
                .build();
    }

    public List<VesselSchedule> getHighRiskVessels() {

        return vesselScheduleRepository
                .findByRiskLevelIn(
                        List.of(
                                RiskLevel.HIGH,
                                RiskLevel.CRITICAL));
    }

    public List<Shipment> getAtRiskShipments() {

        return shipmentRepository
                .findByShipmentStatus(
                        ShipmentStatus.AT_RISK);
    }
}