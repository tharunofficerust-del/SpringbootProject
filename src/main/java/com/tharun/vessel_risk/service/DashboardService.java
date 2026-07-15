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
import com.tharun.vessel_risk.dto.VoyageRiskSummaryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j

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

    public VoyageRiskSummaryResponse getVoyageRiskSummary(
        String voyageNumber) {

        VesselSchedule vessel =
                vesselScheduleRepository
                        .findByVoyageNumber(voyageNumber)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Vessel not found"));

        List<Shipment> shipments =
                shipmentRepository
                        .findByVesselScheduleId(
                                vessel.getId());

        Integer totalDelayHours =
                delayReportRepository
                        .getTotalDelayHoursByVessel(
                                vessel.getId());

        Double delayImpactScore =
                delayReportRepository
                        .getDelayImpactScoreByVessel(
                                vessel.getId());

        int atRisk =
                (int) shipments.stream()
                        .filter(s ->
                                s.getShipmentStatus()
                                        == ShipmentStatus.AT_RISK)
                        .count();

        int delayed =
                (int) shipments.stream()
                        .filter(s ->
                                s.getShipmentStatus()
                                        == ShipmentStatus.DELAYED)
                        .count();

        int onTime =
                shipments.size() - atRisk - delayed;

        int critical =
                (int) shipments.stream()
                        .filter(s ->
                                s.getPriority()
                                        == com.tharun.vessel_risk.enums.Priority.CRITICAL
                                &&
                                (s.getShipmentStatus()
                                        == ShipmentStatus.AT_RISK
                                        ||
                                s.getShipmentStatus()
                                        == ShipmentStatus.DELAYED))
                        .count();

        return VoyageRiskSummaryResponse.builder()  
                .voyageNumber(
                        vessel.getVoyageNumber())
                .vesselName(
                        vessel.getVesselName())
                .plannedArrivalDate(
                        vessel.getPlannedArrivalDate())
                .revisedArrivalDate(
                        vessel.getCurrentEta())
                .totalDelayHours(
                        totalDelayHours)
                .delayImpactScore(
                        Math.round(delayImpactScore * 100.0) / 100.0)
                .totalShipments(
                        shipments.size())
                .onTimeShipments(
                        onTime)
                .atRiskShipments(
                        atRisk)
                .delayedShipments(
                        delayed)
                .criticalShipments(
                        critical)
                .riskLevel(
                        vessel.getRiskLevel())
                .build();
        }
}