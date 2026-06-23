package com.tharun.vessel_risk.entity;

import java.time.LocalDateTime;

import com.tharun.vessel_risk.enums.CargoType;
import com.tharun.vessel_risk.enums.Priority;
import com.tharun.vessel_risk.enums.ShipmentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "shipment_tharun")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "shipment_reference", nullable = false, unique = true)
    private String shipmentReference;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Enumerated(EnumType.STRING)
    @Column(name = "cargo_type", nullable = false)
    private CargoType cargoType;

    @Column(name = "cargo_weight", nullable = false)
    private Double cargoWeight;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    private Priority priority;

    @Column(name = "required_delivery_date")
    private LocalDateTime requiredDeliveryDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "shipment_status", nullable = false)
    private ShipmentStatus shipmentStatus;

    @ManyToOne
    @JoinColumn(name = "vessel_schedule_id", nullable = false)
    private VesselSchedule vesselSchedule;

}