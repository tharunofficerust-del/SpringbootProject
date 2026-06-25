package com.tharun.vessel_risk.dto;

import com.tharun.vessel_risk.enums.ShipmentStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateShipmentStatusRequest {

    private ShipmentStatus shipmentStatus;

}