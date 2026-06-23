package com.tharun.vessel_risk.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.tharun.vessel_risk.enums.RiskLevel;
import com.tharun.vessel_risk.entity.VesselSchedule;

public interface VesselScheduleRepository
        extends JpaRepository<VesselSchedule, Long> {


            //to check the number exists in the database or not
    Optional<VesselSchedule> findByVoyageNumber(String voyageNumber);

    boolean existsByVoyageNumber(String voyageNumber);

    
    //for dashboard to show the count of vessels in each risk level
    long countByRiskLevel(RiskLevel riskLevel);

    List<VesselSchedule> findByRiskLevelIn(List<RiskLevel> riskLevels);
}