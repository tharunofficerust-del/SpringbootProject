package com.tharun.vessel_risk.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tharun.vessel_risk.dto.CreateShipmentRequest;
import com.tharun.vessel_risk.dto.ShipmentResponse;
import com.tharun.vessel_risk.dto.UpdateShipmentStatusRequest;
import com.tharun.vessel_risk.entity.Shipment;
import com.tharun.vessel_risk.entity.VesselSchedule;
import com.tharun.vessel_risk.enums.CargoType;
import com.tharun.vessel_risk.enums.Priority;
import com.tharun.vessel_risk.enums.ShipmentStatus;
import com.tharun.vessel_risk.enums.VesselStatus;
import com.tharun.vessel_risk.exception.BusinessValidationException;
import com.tharun.vessel_risk.exception.DuplicateResourceException;
import com.tharun.vessel_risk.exception.InvalidStatusTransitionException;
import com.tharun.vessel_risk.exception.ResourceNotFoundException;
import com.tharun.vessel_risk.mapper.ShipmentMapper;
import com.tharun.vessel_risk.repository.ShipmentRepository;
import com.tharun.vessel_risk.repository.VesselScheduleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import lombok.extern.slf4j.Slf4j;
import com.tharun.vessel_risk.dto.ShipmentPageResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Slf4j

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

                VesselSchedule vessel = vesselScheduleRepository
                                .findByVoyageNumber(request.getVoyageNumber())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Vessel not found"));

                validateShipmentRequest(request, vessel);
                validateCapacity(request,vessel);

                Shipment shipment = Shipment.builder()
                                .shipmentReference(request.getShipmentReference())
                                .customerName(request.getCustomerName())
                                .cargoType(request.getCargoType())
                                .cargoWeight(request.getCargoWeight())
                                .priority(request.getPriority())
                                .requiredDeliveryDate(
                                                request.getRequiredDeliveryDate())
                                .shipmentStatus(
                                                ShipmentStatus.ASSIGNED_TO_VESSEL)
                                .vesselSchedule(vessel)
                                .build();

                Shipment savedShipment = shipmentRepository.save(shipment);

                log.info(
                                "Shipment created successfully: {} assigned to voyage {}",
                                savedShipment.getShipmentReference(),
                                vessel.getVoyageNumber());

                return shipmentMapper.toResponse(savedShipment);
        }

        private void validateShipmentRequest(
                        CreateShipmentRequest request,
                        VesselSchedule vessel) {

                if (request.getCargoWeight() <= 0) {

                        throw new BusinessValidationException(
                                        "Cargo weight must be greater than zero");
                }

                if (vessel.getScheduleStatus() == VesselStatus.ARRIVED
                                || vessel.getScheduleStatus() == VesselStatus.CANCELLED) {

                        throw new BusinessValidationException(
                                        "Cannot assign shipment to ARRIVED or CANCELLED vessel");
                }

                if (request.getRequiredDeliveryDate() != null
                                && !request.getRequiredDeliveryDate()
                                                .isAfter(vessel.getPlannedDepartureDate())) {

                        throw new BusinessValidationException(
                                        "Required delivery date must be after vessel departure date");
                }

                if (request.getCargoType() == CargoType.HAZARDOUS
                                && request.getPriority() == Priority.LOW) {

                        throw new BusinessValidationException(
                                        "HAZARDOUS cargo cannot have LOW priority");
                }

                if (request.getCargoType() == CargoType.REEFER
                                && request.getPriority() == Priority.LOW) {

                        throw new BusinessValidationException(
                                        "REEFER cargo must have MEDIUM, HIGH, or CRITICAL priority");
                }

                if (request.getPriority() == Priority.CRITICAL
                                && request.getRequiredDeliveryDate() == null) {

                        throw new BusinessValidationException(
                                        "CRITICAL priority shipment must have required delivery date");
                }
                if (request.getCargoType() == CargoType.HAZARDOUS
                                && request.getPriority() != Priority.CRITICAL) {

                        throw new BusinessValidationException(
                                        "Hazardous cargo must have CRITICAL priority");
                }
        }

        public ShipmentResponse updateShipmentStatus(
                        String shipmentReference,
                        UpdateShipmentStatusRequest request) {

                Shipment shipment = shipmentRepository
                                .findByShipmentReference(
                                                shipmentReference)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Shipment not found"));

                validateShipmentStatusTransition(
                                shipment.getShipmentStatus(),
                                request.getShipmentStatus());

                if (request.getShipmentStatus() == ShipmentStatus.DELIVERED
                                && shipment.getVesselSchedule()
                                                .getScheduleStatus() != VesselStatus.ARRIVED) {

                        throw new BusinessValidationException(
                                        "Shipment cannot be DELIVERED before vessel ARRIVED");
                }
                ShipmentStatus oldStatus = shipment.getShipmentStatus();
                shipment.setShipmentStatus(
                                request.getShipmentStatus());

                Shipment updatedShipment = shipmentRepository.save(shipment);

                log.info(
                                "Shipment {} status changed from {} to {}",
                                shipmentReference,
                                oldStatus,
                                request.getShipmentStatus());

                return shipmentMapper.toResponse(
                                updatedShipment);
        }


        //status transition validation - shipment

        private void validateShipmentStatusTransition(
                        ShipmentStatus currentStatus,
                        ShipmentStatus newStatus) {

                switch (currentStatus) {

                        case ASSIGNED_TO_VESSEL:

                                if (newStatus != ShipmentStatus.IN_TRANSIT
                                                && newStatus != ShipmentStatus.CANCELLED) {

                                        throw new InvalidStatusTransitionException(
                                                        "ASSIGNED_TO_VESSEL shipment can only move to IN_TRANSIT or CANCELLED");
                                }

                                break;

                        case IN_TRANSIT:

                                if (newStatus != ShipmentStatus.AT_RISK) {

                                        throw new InvalidStatusTransitionException(
                                                        "IN_TRANSIT shipment can only move to AT_RISK");
                                }

                                break;

                        case AT_RISK:

                                if (newStatus != ShipmentStatus.DELAYED
                                                && newStatus != ShipmentStatus.DELIVERED) {

                                        throw new InvalidStatusTransitionException(
                                                        "AT_RISK shipment can only move to DELAYED or DELIVERED");
                                }

                                break;

                        case DELAYED:

                                if (newStatus != ShipmentStatus.DELIVERED) {

                                        throw new InvalidStatusTransitionException(
                                                        "DELAYED shipment can only move to DELIVERED");
                                }

                                break;

                        case DELIVERED:

                                throw new InvalidStatusTransitionException(
                                                "DELIVERED shipment status cannot be changed");

                        case CANCELLED:

                                throw new InvalidStatusTransitionException(
                                                "CANCELLED shipment status cannot be changed");
                }
        }

        public ShipmentResponse getShipment(
                        String shipmentReference) {

                Shipment shipment = shipmentRepository
                                .findByShipmentReference(
                                                shipmentReference)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Shipment not found"));

                return shipmentMapper.toResponse(shipment);
        }

        public List<ShipmentResponse> getShipmentsByVoyage(
                        String voyageNumber) {

                return shipmentRepository
                                .findByVesselScheduleVoyageNumber(
                                                voyageNumber)
                                .stream()
                                .map(shipmentMapper::toResponse)
                                .toList();
        }

        public ShipmentPageResponse getAllShipments(
                        int page,
                        int size,
                        String sortBy,
                        String direction) {

                Sort sort = direction.equalsIgnoreCase("desc")
                                ? Sort.by(sortBy).descending()
                                : Sort.by(sortBy).ascending();

                Pageable pageable = PageRequest.of(page, size, sort);

                Page<Shipment> shipmentPage = shipmentRepository.findAll(pageable);

                List<ShipmentResponse> shipmentResponses = shipmentPage.getContent()
                                .stream()
                                .map(shipmentMapper::toResponse)
                                .toList();

                return ShipmentPageResponse.builder()
                                .shipments(shipmentResponses)
                                .currentPage(shipmentPage.getNumber())
                                .totalPages(shipmentPage.getTotalPages())
                                .totalElements(shipmentPage.getTotalElements())
                                .last(shipmentPage.isLast())
                                .build();
        }

        private void validateCapacity(
        CreateShipmentRequest request,
        VesselSchedule vessel) {

        double usedCapacity =
        shipmentRepository
                .findByVesselScheduleId(
                        vessel.getId())
                .stream()
                .mapToDouble(
                        shipment -> shipment.getCargoWeight() == null
                                ? 0
                                : shipment.getCargoWeight())
                .sum();

        double totalCapacity =
                usedCapacity
                        + request.getCargoWeight();

        if (totalCapacity
                > vessel.getVesselCapacityTEU()) {

                throw new BusinessValidationException(
                        "Vessel capacity exceeded. Available capacity: "
                                + (vessel.getVesselCapacityTEU()
                                        - usedCapacity));
        }
        }

}