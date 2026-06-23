package com.tharun.vessel_risk.mapper;

import org.springframework.stereotype.Component;

import com.tharun.vessel_risk.dto.ShipmentResponse;
import com.tharun.vessel_risk.entity.Shipment;

@Component
public class ShipmentMapper {

    public ShipmentResponse toResponse(Shipment shipment) {

        return ShipmentResponse.builder()
                .id(shipment.getId())
                .shipmentReference(shipment.getShipmentReference())
                .customerName(shipment.getCustomerName())
                .cargoType(shipment.getCargoType())
                .cargoWeight(shipment.getCargoWeight())
                .priority(shipment.getPriority())
                .requiredDeliveryDate(shipment.getRequiredDeliveryDate())
                .shipmentStatus(shipment.getShipmentStatus())
                .voyageNumber(
                        shipment.getVesselSchedule().getVoyageNumber())
                .build();
    }
}