package com.tharun.vessel_risk.entity;

import java.time.LocalDate;

import com.tharun.vessel_risk.enums.DelayReason;

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
@Table(name = "delay_report_tharun")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DelayReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "delay_reason", nullable = false)
    private DelayReason delayReason;

    @Column(name = "delay_hours", nullable = false)
    private Integer delayHours;

    @Column(name = "reported_port", nullable = false)
    private String reportedPort;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "reported_date", nullable = false)
    private LocalDate reportedDate;

    @ManyToOne
    @JoinColumn(name = "vessel_schedule_id", nullable = false)
    private VesselSchedule vesselSchedule;
}