package com.tharun.vessel_risk.dto;

import java.time.LocalDateTime;

import com.tharun.vessel_risk.enums.CargoType;
import com.tharun.vessel_risk.enums.Priority;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateShipmentRequest {

    private String shipmentReference;

    private String customerName;

    private CargoType cargoType;

    private Double cargoWeight;

    private Priority priority;

    private LocalDateTime requiredDeliveryDate;

    private String voyageNumber;
}