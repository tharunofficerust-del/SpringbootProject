package com.tharun.vessel_risk.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tharun.vessel_risk.dto.CreateShipmentRequest;
import com.tharun.vessel_risk.dto.ShipmentResponse;
import com.tharun.vessel_risk.entity.Shipment;
import com.tharun.vessel_risk.entity.VesselSchedule;
import com.tharun.vessel_risk.enums.ShipmentStatus;
import com.tharun.vessel_risk.exception.DuplicateResourceException;
import com.tharun.vessel_risk.exception.ResourceNotFoundException;
import com.tharun.vessel_risk.mapper.ShipmentMapper;
import com.tharun.vessel_risk.repository.ShipmentRepository;
import com.tharun.vessel_risk.repository.VesselScheduleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final VesselScheduleRepository vesselScheduleRepository;
    private final ShipmentMapper shipmentMapper;

    public ShipmentResponse createShipment(
            CreateShipmentRequest request) {

        if (shipmentRepository.existsByShipmentReference(
                request.getShipmentReference())) {

            throw new DuplicateResourceException(
                    "Shipment reference already exists");
        }

        VesselSchedule vessel =
                vesselScheduleRepository
                        .findByVoyageNumber(request.getVoyageNumber())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Vessel not found"));

        Shipment shipment = Shipment.builder()
                .shipmentReference(request.getShipmentReference())
                .customerName(request.getCustomerName())
                .cargoType(request.getCargoType())
                .cargoWeight(request.getCargoWeight())
                .priority(request.getPriority())
                .requiredDeliveryDate(
                        request.getRequiredDeliveryDate())
                .shipmentStatus(ShipmentStatus.CREATED)
                .vesselSchedule(vessel)
                .build();

        Shipment savedShipment =
                shipmentRepository.save(shipment);

        return shipmentMapper.toResponse(savedShipment);
    }

    public ShipmentResponse getShipment(
            String shipmentReference) {

        Shipment shipment =
                shipmentRepository
                        .findByShipmentReference(shipmentReference)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Shipment not found"));

        return shipmentMapper.toResponse(shipment);
    }

    public List<ShipmentResponse> getShipmentsByVoyage(
            String voyageNumber) {

        return shipmentRepository
                .findByVesselScheduleVoyageNumber(voyageNumber)
                .stream()
                .map(shipmentMapper::toResponse)
                .toList();
    }
}