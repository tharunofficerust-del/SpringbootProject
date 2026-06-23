package com.tharun.vessel_risk.mapper;

import org.springframework.stereotype.Component;

import com.tharun.vessel_risk.dto.CreateVesselRequest;
import com.tharun.vessel_risk.dto.VesselResponse;
import com.tharun.vessel_risk.entity.VesselSchedule;
import com.tharun.vessel_risk.enums.RiskLevel;

@Component
public class VesselScheduleMapper {

    public VesselSchedule toEntity(CreateVesselRequest request) {

        return VesselSchedule.builder()
                .vesselName(request.getVesselName())
                .voyageNumber(request.getVoyageNumber())
                .originPort(request.getOriginPort())
                .destinationPort(request.getDestinationPort())
                .plannedDepartureDate(request.getPlannedDepartureDate())
                .plannedArrivalDate(request.getPlannedArrivalDate())
                .currentEta(request.getPlannedArrivalDate())
                .vesselCapacityTEU(request.getVesselCapacityTEU())
                .scheduleStatus(request.getScheduleStatus())
                .riskLevel(RiskLevel.LOW)
                .build();
    }

    public VesselResponse toResponse(VesselSchedule vesselSchedule) {

        return VesselResponse.builder()
                .id(vesselSchedule.getId())
                .vesselName(vesselSchedule.getVesselName())
                .voyageNumber(vesselSchedule.getVoyageNumber())
                .originPort(vesselSchedule.getOriginPort())
                .destinationPort(vesselSchedule.getDestinationPort())
                .plannedDepartureDate(vesselSchedule.getPlannedDepartureDate())
                .plannedArrivalDate(vesselSchedule.getPlannedArrivalDate())
                .currentEta(vesselSchedule.getCurrentEta())
                .vesselCapacityTEU(vesselSchedule.getVesselCapacityTEU())
                .scheduleStatus(vesselSchedule.getScheduleStatus())
                .riskLevel(vesselSchedule.getRiskLevel())
                .build();
    }
}