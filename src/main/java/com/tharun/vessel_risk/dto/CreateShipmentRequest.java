package com.tharun.vessel_risk.dto;

import java.time.LocalDateTime;

import com.tharun.vessel_risk.enums.CargoType;
import com.tharun.vessel_risk.enums.Priority;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateShipmentRequest {

    @NotBlank(message = "Shipment reference cannot be blank")
    private String shipmentReference;

    @NotBlank(message = "Customer name cannot be blank")
    private String customerName;

    @NotNull(message = "Cargo type is required")
    private CargoType cargoType;

    @Positive(message = "Cargo weight must be greater than zero")
    private Double cargoWeight;

    @NotNull(message = "Priority is required")
    private Priority priority;

    @NotNull(message = "Required delivery date is required")
    private LocalDateTime requiredDeliveryDate;

    @NotBlank(message = "Voyage number cannot be blank")
    private String voyageNumber;
}