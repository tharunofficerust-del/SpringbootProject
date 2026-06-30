package com.tharun.vessel_risk.mapper;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.tharun.vessel_risk.dto.ShipmentResponse;
import com.tharun.vessel_risk.entity.Shipment;

@Component
public class ShipmentMapper {

    public ShipmentResponse toResponse(Shipment shipment) {

        String classification = getDeliveryClassification(shipment);

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
                .deliveryClassification(classification)
                .build();
    }

    private String getDeliveryClassification(Shipment shipment) {

        LocalDateTime currentEta = shipment.getVesselSchedule().getCurrentEta();

        LocalDateTime requiredDate = shipment.getRequiredDeliveryDate();

        if (currentEta.isAfter(requiredDate)) {
            return "AT_RISK";
        }

        return "ON_TIME";
    }
}