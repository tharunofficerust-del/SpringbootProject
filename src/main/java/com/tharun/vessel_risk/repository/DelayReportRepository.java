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

        @Query("""
                        SELECT COALESCE(
                                SUM(
                                CASE
                                        WHEN d.delayReason = 'PORT_CONGESTION'
                                        THEN d.delayHours * 1.0

                                        WHEN d.delayReason = 'BAD_WEATHER'
                                        THEN d.delayHours * 1.2

                                        WHEN d.delayReason = 'CUSTOMS_HOLD'
                                        THEN d.delayHours * 1.3

                                        WHEN d.delayReason = 'VESSEL_BREAKDOWN'
                                        THEN d.delayHours * 1.5

                                        WHEN d.delayReason = 'DOCUMENT_ISSUE'
                                        THEN d.delayHours * 0.8
                                END
                                ),
                                0
                        )
                        FROM DelayReport d
                        WHERE d.vesselSchedule.id = :vesselId
                        """)
        Double getDelayImpactScoreByVessel(
                        @Param("vesselId") Long vesselId);
}