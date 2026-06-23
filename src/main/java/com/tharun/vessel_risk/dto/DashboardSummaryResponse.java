package com.tharun.vessel_risk.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DashboardSummaryResponse {

    private long totalVessels;

    private long totalShipments;

    private long totalDelayReports;

    private long highRiskVessels;

    private long criticalRiskVessels;

    private long atRiskShipments;
}