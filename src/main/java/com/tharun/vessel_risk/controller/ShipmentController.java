package com.tharun.vessel_risk.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tharun.vessel_risk.dto.CreateShipmentRequest;
import com.tharun.vessel_risk.dto.ShipmentResponse;
import com.tharun.vessel_risk.dto.UpdateShipmentStatusRequest;
import com.tharun.vessel_risk.service.ShipmentService;
import com.tharun.vessel_risk.dto.ShipmentPageResponse;
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

    @PutMapping("/{shipmentReference}/status")
    public ResponseEntity<ShipmentResponse>
    updateShipmentStatus(

            @PathVariable String shipmentReference,

            @RequestBody UpdateShipmentStatusRequest request) {

        return ResponseEntity.ok(
                shipmentService.updateShipmentStatus(
                        shipmentReference,
                        request));
    }

    @GetMapping
public ResponseEntity<ShipmentPageResponse>
getAllShipments(

        @RequestParam(defaultValue = "0")
        int page,

        @RequestParam(defaultValue = "5")
        int size,

        @RequestParam(defaultValue = "id")
        String sortBy,

        @RequestParam(defaultValue = "asc")
        String direction) {

    ShipmentPageResponse response =
            shipmentService.getAllShipments(
                    page,
                    size,
                    sortBy,
                    direction);

    return ResponseEntity.ok(response);
}
}