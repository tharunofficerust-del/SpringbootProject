package com.tharun.vessel_risk.dto;

import java.time.LocalDateTime;

import com.tharun.vessel_risk.enums.VesselStatus;

import lombok.Getter;
import lombok.Setter;


//used for : POST /api/vessels/schedules

@Getter
@Setter
public class CreateVesselRequest {

    private String vesselName;

    private String voyageNumber;

    private String originPort;

    private String destinationPort;

    private LocalDateTime plannedDepartureDate;

    private LocalDateTime plannedArrivalDate;

    private Integer vesselCapacityTEU;

    private VesselStatus scheduleStatus;
}