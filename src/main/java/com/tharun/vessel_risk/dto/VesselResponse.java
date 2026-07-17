package com.tharun.vessel_risk.dto;

import java.time.LocalDateTime;

import com.tharun.vessel_risk.enums.RiskLevel;
import com.tharun.vessel_risk.enums.VesselStatus;

import lombok.Builder;
import lombok.Getter;

// used for 
// GET
// POST Response

@Getter
@Builder
public class VesselResponse {

    private Long id;

    private String vesselName;

    private String voyageNumber;

    private String originPort;

    private String destinationPort;

    private LocalDateTime plannedDepartureDate;

    private LocalDateTime plannedArrivalDate;

    private LocalDateTime currentEta;

    private Integer vesselCapacityTEU;

    private VesselStatus scheduleStatus;

    private RiskLevel riskLevel;

    //private Integer totalDelayHours;
}