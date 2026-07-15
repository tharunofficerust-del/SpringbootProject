package com.tharun.vessel_risk.dto;

import com.tharun.vessel_risk.enums.VesselStatus;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

//used for : PUT /api/vessels/schedules/{voyageNumber}/status

@Getter
@Setter
public class UpdateVesselStatusRequest {

    @NotBlank(message = "Schedule status is required")
    private VesselStatus scheduleStatus;
}