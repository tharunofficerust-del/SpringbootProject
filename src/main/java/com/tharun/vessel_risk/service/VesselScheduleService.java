package com.tharun.vessel_risk.service;

import org.springframework.stereotype.Service;
import java.util.List;

import com.tharun.vessel_risk.dto.CreateVesselRequest;
import com.tharun.vessel_risk.dto.UpdateVesselStatusRequest;
import com.tharun.vessel_risk.dto.VesselResponse;
import com.tharun.vessel_risk.entity.Shipment;
import com.tharun.vessel_risk.entity.VesselSchedule;
import com.tharun.vessel_risk.enums.RiskLevel;
import com.tharun.vessel_risk.enums.ShipmentStatus;
import com.tharun.vessel_risk.enums.VesselStatus;
import com.tharun.vessel_risk.exception.BusinessValidationException;
import com.tharun.vessel_risk.exception.DuplicateResourceException;
import com.tharun.vessel_risk.exception.InvalidStatusTransitionException;
import com.tharun.vessel_risk.exception.ResourceNotFoundException;
import com.tharun.vessel_risk.mapper.VesselScheduleMapper;
import com.tharun.vessel_risk.repository.ShipmentRepository;
import com.tharun.vessel_risk.repository.VesselScheduleRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.tharun.vessel_risk.dto.VesselPageResponse;

@Service
@RequiredArgsConstructor
@Slf4j

public class VesselScheduleService {

    private final VesselScheduleRepository vesselScheduleRepository;

    private final VesselScheduleMapper vesselScheduleMapper;

    private final ShipmentRepository shipmentRepository;

    public VesselResponse createVesselSchedule(
            CreateVesselRequest request) {

        validateVesselRequest(request);

        VesselSchedule vesselSchedule =
                vesselScheduleMapper.toEntity(request);

        VesselSchedule savedVessel =
        vesselScheduleRepository.save(vesselSchedule);

        log.info(
                "Vessel schedule created: {} ({})",
                savedVessel.getVesselName(),
                savedVessel.getVoyageNumber());

        return vesselScheduleMapper.toResponse(savedVessel);
    }

    private void validateVesselRequest(
            CreateVesselRequest request) {

        log.warn(
        "Duplicate voyage number attempted: {}",
        request.getVoyageNumber());

        if (vesselScheduleRepository.existsByVoyageNumber(
                request.getVoyageNumber())) {

            throw new DuplicateResourceException(
                    "Voyage number already exists : "
                            + request.getVoyageNumber());
        }

        if (request.getOriginPort()
                .equalsIgnoreCase(request.getDestinationPort())) {

            throw new BusinessValidationException(
                    "Origin port and destination port cannot be the same");
        }

        if (!request.getPlannedArrivalDate()
                .isAfter(request.getPlannedDepartureDate())) {

            throw new BusinessValidationException(
                    "Planned arrival date must be after planned departure date");
        }

        if (request.getVesselCapacityTEU() <= 0) {

            throw new BusinessValidationException(
                    "Vessel capacity must be greater than zero");
        }
    }

    public VesselResponse getVesselByVoyageNumber(
            String voyageNumber) {

        VesselSchedule vesselSchedule =
                vesselScheduleRepository
                        .findByVoyageNumber(voyageNumber)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Vessel not found with voyage number : "
                                                + voyageNumber));

        return vesselScheduleMapper.toResponse(
                vesselSchedule);
    }

    public VesselPageResponse getAllVessels(
            int page,
            int size,
            String sortBy,
            String sortDirection) {

        Sort sort = sortDirection.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable =
                PageRequest.of(page, size, sort);

        Page<VesselSchedule> vesselPage =
                vesselScheduleRepository.findAll(pageable);

        List<VesselResponse> vesselResponses =
                vesselPage.getContent()
                        .stream()
                        .map(vesselScheduleMapper::toResponse)
                        .toList();

        return VesselPageResponse.builder()
                .vessels(vesselResponses)
                .currentPage(vesselPage.getNumber())
                .totalPages(vesselPage.getTotalPages())
                .totalElements(vesselPage.getTotalElements())
                .last(vesselPage.isLast())
                .build();
    }

    public VesselResponse updateVesselStatus(
            String voyageNumber,
            UpdateVesselStatusRequest request) {

        VesselSchedule vesselSchedule =
                vesselScheduleRepository
                        .findByVoyageNumber(voyageNumber)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Vessel not found with voyage number : "
                                                + voyageNumber));

        validateStatusTransition(
                vesselSchedule.getScheduleStatus(),
                request.getScheduleStatus());
        
        VesselStatus oldStatus = vesselSchedule.getScheduleStatus();

        vesselSchedule.setScheduleStatus(
                request.getScheduleStatus());

        if (request.getScheduleStatus()
                == VesselStatus.CANCELLED) {

            cancelAssignedShipments(
                    vesselSchedule);
        }

        VesselSchedule updatedVessel =
        vesselScheduleRepository.save(vesselSchedule);

        log.info(
                "Vessel {} status changed from {} to {}",
                voyageNumber,
                oldStatus,
                request.getScheduleStatus());

        return vesselScheduleMapper.toResponse(updatedVessel);
    }

    private void cancelAssignedShipments(
            VesselSchedule vesselSchedule) {

        List<Shipment> shipments =
                shipmentRepository
                        .findByVesselScheduleId(
                                vesselSchedule.getId());

        shipments.forEach(shipment -> {

            if (shipment.getShipmentStatus()
                    != ShipmentStatus.DELIVERED) {

                shipment.setShipmentStatus(
                        ShipmentStatus.CANCELLED);
            }
        });

        shipmentRepository.saveAll(shipments);
    }

    private void validateStatusTransition(
            VesselStatus currentStatus,
            VesselStatus newStatus) {

        switch (currentStatus) {

            case PLANNED:

                if (newStatus != VesselStatus.DEPARTED
                        && newStatus != VesselStatus.CANCELLED) {

                    throw new InvalidStatusTransitionException(
                            "PLANNED vessel can only move to DEPARTED or CANCELLED");
                }
                break;

            case DEPARTED:

                if (newStatus != VesselStatus.IN_TRANSIT
                        && newStatus != VesselStatus.CANCELLED) {

                    throw new InvalidStatusTransitionException(
                            "DEPARTED vessel can only move to IN_TRANSIT or CANCELLED");
                }
                break;

            case IN_TRANSIT:

                if (newStatus != VesselStatus.ARRIVED) {

                    throw new InvalidStatusTransitionException(
                            "IN_TRANSIT vessel can only move to ARRIVED");
                }
                break;

            case ARRIVED:

                throw new InvalidStatusTransitionException(
                        "ARRIVED vessel status cannot be changed");

            case CANCELLED:

                throw new InvalidStatusTransitionException(
                        "CANCELLED vessel status cannot be changed");
        }
    }
}