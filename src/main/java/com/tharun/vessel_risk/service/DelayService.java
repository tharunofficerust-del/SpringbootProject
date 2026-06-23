package com.tharun.vessel_risk.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tharun.vessel_risk.dto.CreateDelayReportRequest;
import com.tharun.vessel_risk.dto.DelayResponse;
import com.tharun.vessel_risk.entity.DelayReport;
import com.tharun.vessel_risk.entity.Shipment;
import com.tharun.vessel_risk.entity.VesselSchedule;
import com.tharun.vessel_risk.enums.RiskLevel;
import com.tharun.vessel_risk.enums.ShipmentStatus;
import com.tharun.vessel_risk.exception.BusinessValidationException;
import com.tharun.vessel_risk.exception.ResourceNotFoundException;
import com.tharun.vessel_risk.mapper.DelayMapper;
import com.tharun.vessel_risk.repository.DelayReportRepository;
import com.tharun.vessel_risk.repository.ShipmentRepository;
import com.tharun.vessel_risk.repository.VesselScheduleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DelayService {

    private final DelayReportRepository delayReportRepository;

    private final VesselScheduleRepository vesselScheduleRepository;

    private final ShipmentRepository shipmentRepository;

    private final DelayMapper delayMapper;

    public DelayResponse createDelayReport(
            CreateDelayReportRequest request) {

        VesselSchedule vessel =
                vesselScheduleRepository
                        .findByVoyageNumber(
                                request.getVoyageNumber())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Vessel not found"));

        if (request.getDelayHours() <= 0) {

            throw new BusinessValidationException(
                    "Delay hours must be greater than zero");
        }

        boolean duplicate =
                delayReportRepository
                        .existsByVesselScheduleAndDelayReasonAndReportedPortAndReportedDate(
                                vessel,
                                request.getDelayReason(),
                                request.getReportedPort(),
                                request.getReportedDate());

        if (duplicate) {

            throw new BusinessValidationException(
                    "Duplicate delay report found");
        }

        DelayReport delayReport =
                DelayReport.builder()
                        .delayReason(request.getDelayReason())
                        .delayHours(request.getDelayHours())
                        .reportedPort(request.getReportedPort())
                        .remarks(request.getRemarks())
                        .reportedDate(request.getReportedDate())
                        .vesselSchedule(vessel)
                        .build();

        DelayReport saved =
                delayReportRepository.save(delayReport);

        recalculateEta(vessel);

        updateRiskLevel(vessel);

        updateShipmentRiskStatus(vessel);

        return delayMapper.toResponse(saved);
    }

    public List<DelayResponse> getDelayReports(
            String voyageNumber) {

        return delayReportRepository
                .findByVesselScheduleVoyageNumber(
                        voyageNumber)
                .stream()
                .map(delayMapper::toResponse)
                .toList();
    }

    private void recalculateEta(
            VesselSchedule vesselSchedule) {

        Integer totalDelayHours =
                delayReportRepository
                        .getTotalDelayHoursByVessel(
                                vesselSchedule.getId());

        vesselSchedule.setCurrentEta(
                vesselSchedule.getPlannedArrivalDate()
                        .plusHours(totalDelayHours));

        vesselScheduleRepository.save(vesselSchedule);
    }

    private void updateRiskLevel(
            VesselSchedule vesselSchedule) {

        Integer totalDelayHours =
                delayReportRepository
                        .getTotalDelayHoursByVessel(
                                vesselSchedule.getId());

        if (totalDelayHours <= 6) {

            vesselSchedule.setRiskLevel(RiskLevel.LOW);

        } else if (totalDelayHours <= 24) {

            vesselSchedule.setRiskLevel(RiskLevel.MEDIUM);

        } else if (totalDelayHours <= 48) {

            vesselSchedule.setRiskLevel(RiskLevel.HIGH);

        } else {

            vesselSchedule.setRiskLevel(RiskLevel.CRITICAL);
        }

        vesselScheduleRepository.save(vesselSchedule);
    }

    private void updateShipmentRiskStatus(
            VesselSchedule vesselSchedule) {

        if (vesselSchedule.getRiskLevel() != RiskLevel.HIGH
                && vesselSchedule.getRiskLevel() != RiskLevel.CRITICAL) {

            return;
        }

        List<Shipment> shipments =
                shipmentRepository.findByVesselScheduleId(
                        vesselSchedule.getId());

        shipments.forEach(shipment ->
                shipment.setShipmentStatus(
                        ShipmentStatus.AT_RISK));

        shipmentRepository.saveAll(shipments);
    }
}