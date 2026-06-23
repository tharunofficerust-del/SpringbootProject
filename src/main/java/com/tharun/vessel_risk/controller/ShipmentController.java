package com.tharun.vessel_risk.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tharun.vessel_risk.dto.CreateShipmentRequest;
import com.tharun.vessel_risk.dto.ShipmentResponse;
import com.tharun.vessel_risk.service.ShipmentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/shipments")
@RequiredArgsConstructor
public class ShipmentController {

    private final ShipmentService shipmentService;

    @PostMapping
    public ResponseEntity<ShipmentResponse> createShipment(
            @RequestBody CreateShipmentRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(shipmentService.createShipment(request));
    }

    @GetMapping("/{shipmentReference}")
    public ResponseEntity<ShipmentResponse> getShipment(
            @PathVariable String shipmentReference) {

        return ResponseEntity.ok(
                shipmentService.getShipment(shipmentReference));
    }

    @GetMapping("/vessel/{voyageNumber}")
    public ResponseEntity<List<ShipmentResponse>>
    getShipmentsByVoyage(
            @PathVariable String voyageNumber) {

        return ResponseEntity.ok(
                shipmentService.getShipmentsByVoyage(
                        voyageNumber));
    }
}