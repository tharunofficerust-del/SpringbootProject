package com.tharun.vessel_risk.dto;

import java.time.LocalDateTime;

import com.tharun.vessel_risk.enums.RiskLevel;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VoyageRiskSummaryResponse {

    private String voyageNumber;

    private String vesselName;

    private LocalDateTime plannedArrivalDate;

    private LocalDateTime revisedArrivalDate;

    private Integer totalDelayHours;

    private Double delayImpactScore;

    private Integer totalShipments;

    private Integer onTimeShipments;

    private Integer atRiskShipments;

    private Integer delayedShipments;

    private Integer criticalShipments;

    private RiskLevel riskLevel;
}