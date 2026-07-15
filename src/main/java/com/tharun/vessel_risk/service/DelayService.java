package com.tharun.vessel_risk.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tharun.vessel_risk.dto.CreateDelayReportRequest;
import com.tharun.vessel_risk.dto.DelayResponse;
import com.tharun.vessel_risk.dto.UpdateDelayRequest;
import com.tharun.vessel_risk.entity.DelayReport;
import com.tharun.vessel_risk.entity.Shipment;
import com.tharun.vessel_risk.entity.VesselSchedule;
import com.tharun.vessel_risk.enums.CargoType;
import com.tharun.vessel_risk.enums.RiskLevel;
import com.tharun.vessel_risk.enums.ShipmentStatus;
import com.tharun.vessel_risk.enums.VesselStatus;
import com.tharun.vessel_risk.exception.BusinessValidationException;
import com.tharun.vessel_risk.exception.ResourceNotFoundException;
import com.tharun.vessel_risk.mapper.DelayMapper;
import com.tharun.vessel_risk.repository.DelayReportRepository;
import com.tharun.vessel_risk.repository.ShipmentRepository;
import com.tharun.vessel_risk.repository.VesselScheduleRepository;
import com.tharun.vessel_risk.enums.Priority;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
@Service
@RequiredArgsConstructor
@Slf4j
public class DelayService {

    private final DelayReportRepository delayReportRepository;

    private final VesselScheduleRepository vesselScheduleRepository;

    private final ShipmentRepository shipmentRepository;

    private final DelayMapper delayMapper;

    @Transactional
    public DelayResponse createDelayReport(
            CreateDelayReportRequest request) {

        VesselSchedule vessel =
                vesselScheduleRepository
                        .findByVoyageNumber(
                                request.getVoyageNumber())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Vessel not found"));

        validateDelayRequest(request, vessel);

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

        log.info(
                "Delay report created for voyage {} with {} hours",
                request.getVoyageNumber(),
                request.getDelayHours());

        recalculateEta(vessel);

        updateRiskLevel(vessel);

        updateShipmentRiskStatus(vessel);

        return delayMapper.toResponse(saved);
    }

    private void validateDelayRequest(
            CreateDelayReportRequest request,
            VesselSchedule vessel) {

        if (vessel.getScheduleStatus() != VesselStatus.DEPARTED
                && vessel.getScheduleStatus() != VesselStatus.IN_TRANSIT) {

            throw new BusinessValidationException(
                    "Delay can only be reported for DEPARTED or IN_TRANSIT vessels");
        }

        if (request.getDelayHours() <= 0) {

            throw new BusinessValidationException(
                    "Delay hours must be greater than zero");
        }

        if (request.getDelayHours() > 120) {

            throw new BusinessValidationException(
                    "Maximum delay hours allowed is 120");
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
                        .getTotalDelayHoursByVessel(              //based on the delay
                                vesselSchedule.getId());

        vesselSchedule.setCurrentEta(
                vesselSchedule.getPlannedArrivalDate()
                        .plusHours(totalDelayHours));            // adds the delayhours

        vesselScheduleRepository.save(vesselSchedule);
        log.info(
        "ETA recalculated for voyage {}. New ETA: {}",
        vesselSchedule.getVoyageNumber(),
        vesselSchedule.getCurrentEta());
    }


    // set delay calculation 

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
        log.info(
                "Risk level updated for voyage {} -> {}",
                vesselSchedule.getVoyageNumber(),
                vesselSchedule.getRiskLevel());
        }

    private void updateShipmentRiskStatus(
        VesselSchedule vesselSchedule) {

        Integer totalDelayHours =
                delayReportRepository
                        .getTotalDelayHoursByVessel(
                                vesselSchedule.getId());

        List<Shipment> shipments =
                shipmentRepository.findByVesselScheduleId(
                        vesselSchedule.getId());

        shipments.forEach(shipment -> {

                if (shipment.getRequiredDeliveryDate() == null) {

                        return;
                }

                boolean etaExceeded =
                        vesselSchedule.getCurrentEta()
                                .isAfter(
                                        shipment.getRequiredDeliveryDate());

                if (!etaExceeded) {

                shipment.setShipmentStatus(
                        ShipmentStatus.IN_TRANSIT);

                return;
                }

                // HAZARDOUS cargo
                if (shipment.getCargoType() == CargoType.HAZARDOUS) {

                shipment.setShipmentStatus(
                        ShipmentStatus.DELAYED);

                return;
                }

                // REEFER cargo
                if (shipment.getCargoType() == CargoType.REEFER
                        && totalDelayHours > 24) {

                shipment.setShipmentStatus(
                        ShipmentStatus.DELAYED);

                return;
                }

                // CRITICAL priority
                if (shipment.getPriority() == Priority.CRITICAL) {

                shipment.setShipmentStatus(
                        ShipmentStatus.DELAYED);

                return;
                }

                // FRAGILE cargo
                if (shipment.getCargoType() == CargoType.FRAGILE) {

                shipment.setShipmentStatus(
                        ShipmentStatus.AT_RISK);

                return;
                }

                // General rule
                if (totalDelayHours <= 48) {

                shipment.setShipmentStatus(
                        ShipmentStatus.AT_RISK);

                } else {

                shipment.setShipmentStatus(
                        ShipmentStatus.DELAYED);
                }

        });

        shipmentRepository.saveAll(shipments);

        log.info(
        "Shipment risk classification completed for voyage {}",
        vesselSchedule.getVoyageNumber());
        }

//     public void recalculateEtaForVoyage(
//         String voyageNumber) {

//         VesselSchedule vesselSchedule =
//                 vesselScheduleRepository
//                         .findByVoyageNumber(
//                                 voyageNumber)
//                         .orElseThrow(() ->
//                                 new ResourceNotFoundException(
//                                         "Vessel not found"));

//         recalculateEta(vesselSchedule);

//         updateRiskLevel(vesselSchedule);

//         updateShipmentRiskStatus(vesselSchedule);
//         }

        @Transactional
        public void deleteDelayReport(
                Long delayId) {

        DelayReport delayReport =
                delayReportRepository
                        .findById(delayId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Delay report not found"));

        VesselSchedule vessel =
                delayReport.getVesselSchedule();

        delayReportRepository.delete(
                delayReport);

        recalculateEta(vessel);

        updateRiskLevel(vessel);

        updateShipmentRiskStatus(vessel);

        log.info(
                "Delay report {} deleted successfully",
                delayId);
        }

        // Update delay report after entering delay hours with remarks.
        // This will recalculate ETA, risk level and shipment risk status.
        
        @Transactional
        public DelayResponse updateDelayReport(
                Long delayId,
                UpdateDelayRequest request) {

        DelayReport delayReport =
                delayReportRepository.findById(delayId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Delay report not found"));

        VesselSchedule vessel =
                delayReport.getVesselSchedule();

        if (vessel.getScheduleStatus() == VesselStatus.ARRIVED
                || vessel.getScheduleStatus() == VesselStatus.CANCELLED) {

                throw new BusinessValidationException(
                        "Cannot update delays for ARRIVED or CANCELLED vessels.");
        }

        delayReport.setDelayHours(
                request.getDelayHours());

        delayReport.setRemarks(
                request.getRemarks());

        DelayReport updatedDelay =
                delayReportRepository.save(delayReport);

        recalculateEta(vessel);

        updateRiskLevel(vessel);

        updateShipmentRiskStatus(vessel);

        return delayMapper.toResponse(updatedDelay);
        }
}