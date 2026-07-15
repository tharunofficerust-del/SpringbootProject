package com.tharun.vessel_risk.dto;

import com.tharun.vessel_risk.enums.ShipmentStatus;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateShipmentStatusRequest {

    @NotBlank(message = "Shipment status is required")
    private ShipmentStatus shipmentStatus;
}