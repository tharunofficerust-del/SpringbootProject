package com.tharun.vessel_risk.dto;

import java.time.LocalDateTime;

import com.tharun.vessel_risk.enums.CargoType;
import com.tharun.vessel_risk.enums.Priority;
import com.tharun.vessel_risk.enums.ShipmentStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ShipmentResponse {

    private Long id;

    private String shipmentReference;

    private String customerName;

    private CargoType cargoType;

    private Double cargoWeight;

    private Priority priority;

    private LocalDateTime requiredDeliveryDate;

    private ShipmentStatus shipmentStatus;

    private String voyageNumber;
}