package com.tharun.vessel_risk.dto;

import java.time.LocalDateTime;

import com.tharun.vessel_risk.enums.VesselStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

//used for : POST /api/vessels/schedules

@Getter
@Setter
public class CreateVesselRequest {

    @NotBlank(message = "Vessel name cannot be blank")
    private String vesselName;

    @NotBlank(message = "Voyage number cannot be blank")
    private String voyageNumber;

    @NotBlank(message = "Origin port cannot be blank")
    private String originPort;

    @NotBlank(message = "Destination port cannot be blank")
    private String destinationPort;

    @NotNull(message = "Planned departure date is required")
    private LocalDateTime plannedDepartureDate;

    @NotNull(message = "Planned arrival date is required")
    private LocalDateTime plannedArrivalDate;

    @NotBlank(message = "Status is required")
    private VesselStatus scheduleStatus;

    @Positive(message = "Capacity must be greater than zero")
    private Integer vesselCapacityTEU;


}