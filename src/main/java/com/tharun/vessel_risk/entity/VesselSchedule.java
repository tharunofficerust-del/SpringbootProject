package com.tharun.vessel_risk.entity;

import java.time.LocalDateTime;

import com.tharun.vessel_risk.enums.RiskLevel;
import com.tharun.vessel_risk.enums.VesselStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

import jakarta.persistence.OneToMany;

@Entity
@Table(name = "vessel_schedule_tharun")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VesselSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vessel_name", nullable = false)
    private String vesselName;

    @Column(name = "voyage_number", nullable = false, unique = true)
    private String voyageNumber;

    @Column(name = "origin_port", nullable = false)
    private String originPort;

    @Column(name = "destination_port", nullable = false)
    private String destinationPort;

    @Column(name = "planned_departure_date", nullable = false)
    private LocalDateTime plannedDepartureDate;

    @Column(name = "planned_arrival_date", nullable = false)
    private LocalDateTime plannedArrivalDate;

    @Column(name = "vessel_capacity_teu", nullable = false)
    private Integer vesselCapacityTEU;

    @Enumerated(EnumType.STRING)
    @Column(name = "schedule_status", nullable = false)
    private VesselStatus scheduleStatus;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level")
    private RiskLevel riskLevel;




    //relationship with Shipment entity
    @OneToMany(mappedBy = "vesselSchedule")
    private List<Shipment> shipments;

    @OneToMany(mappedBy = "vesselSchedule")
    private List<DelayReport> delayReports;

    //eta calculation method
    @Column(name = "current_eta")
    private LocalDateTime currentEta;

}