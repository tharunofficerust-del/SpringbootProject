package com.tharun.vessel_risk.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tharun.vessel_risk.entity.DelayReport;
import com.tharun.vessel_risk.entity.VesselSchedule;
import com.tharun.vessel_risk.enums.DelayReason;

public interface DelayReportRepository
        extends JpaRepository<DelayReport, Long> {

    List<DelayReport> findByVesselScheduleVoyageNumber(String voyageNumber);

    boolean existsByVesselScheduleAndDelayReasonAndReportedPortAndReportedDate(
            VesselSchedule vesselSchedule,
            DelayReason delayReason,
            String reportedPort,
            LocalDate reportedDate);

    @Query("""
            SELECT COALESCE(SUM(d.delayHours), 0)
            FROM DelayReport d
            WHERE d.vesselSchedule.id = :vesselId
            """)
    Integer getTotalDelayHoursByVessel(
            @Param("vesselId") Long vesselId);
}