package com.tharun.vessel_risk.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.tharun.vessel_risk.dto.CreateVesselRequest;
import com.tharun.vessel_risk.dto.VesselPageResponse;
import com.tharun.vessel_risk.dto.VesselResponse;
import com.tharun.vessel_risk.service.VesselScheduleService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.tharun.vessel_risk.dto.UpdateVesselStatusRequest;


@Valid
@RestController
@RequestMapping("/api/vessels")
@RequiredArgsConstructor
public class VesselScheduleController {

        private final VesselScheduleService vesselScheduleService;


        @PostMapping("/schedules")
        public ResponseEntity<VesselResponse> createVesselSchedule(
                        @Valid @RequestBody CreateVesselRequest request) {

                VesselResponse response = vesselScheduleService.createVesselSchedule(request);

                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(response);
        }

        @GetMapping("/schedules/{voyageNumber}")
        public ResponseEntity<VesselResponse> getVesselByVoyageNumber(
                        @PathVariable String voyageNumber) {

                VesselResponse response = vesselScheduleService.getVesselByVoyageNumber(voyageNumber);

                return ResponseEntity.ok(response);
        }

        // pagination :

        @GetMapping("/schedules")
        public ResponseEntity<VesselPageResponse> getAllVessels(

                        @RequestParam(defaultValue = "0") int page,

                        @RequestParam(defaultValue = "5") int size,

                        @RequestParam(defaultValue = "id") String sortBy,

                        @RequestParam(defaultValue = "asc") String direction) {

                VesselPageResponse response = vesselScheduleService.getAllVessels(
                                page,
                                size,
                                sortBy,
                                direction);

                return ResponseEntity.ok(response);
        }

        @PutMapping("/schedules/{voyageNumber}/status")
        public ResponseEntity<VesselResponse> updateVesselStatus(

                        @PathVariable String voyageNumber,

                        @RequestBody UpdateVesselStatusRequest request) {

                VesselResponse response = vesselScheduleService.updateVesselStatus(
                                voyageNumber,
                                request);

                return ResponseEntity.ok(response);
        }


        //ETA calculated automatically when delay report is created so this
        // is not useful. maybe used for future enhancements.

        // @PostMapping("/{voyageNumber}/recalculate-eta")
        // public ResponseEntity<String> recalculateEta(

        //                 @PathVariable String voyageNumber) {

        //         delayService.recalculateEtaForVoyage(
        //                         voyageNumber);

        //         return ResponseEntity.ok(
        //                         "ETA recalculated successfully");
        // }
}