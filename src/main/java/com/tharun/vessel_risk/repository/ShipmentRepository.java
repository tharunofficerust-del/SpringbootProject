package com.tharun.vessel_risk.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tharun.vessel_risk.enums.ShipmentStatus;
import com.tharun.vessel_risk.entity.Shipment;

public interface ShipmentRepository
        extends JpaRepository<Shipment, Long> {


    //to automatically navigate to the shipment details page when the shipment reference is entered in the search bar
    Optional<Shipment> findByShipmentReference(String shipmentReference);

    boolean existsByShipmentReference(String shipmentReference);

    List<Shipment> findByVesselScheduleVoyageNumber(String voyageNumber);

    List<Shipment> findByVesselScheduleId(Long vesselId);


    //for dashboard to show the count of shipments in each status
    long countByShipmentStatus(ShipmentStatus shipmentStatus);

    List<Shipment> findByShipmentStatus(ShipmentStatus shipmentStatus);
}