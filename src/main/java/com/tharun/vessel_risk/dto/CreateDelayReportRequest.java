package com.tharun.vessel_risk.dto;

import java.time.LocalDate;

import com.tharun.vessel_risk.enums.DelayReason;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateDelayReportRequest {

    private String voyageNumber;

    private DelayReason delayReason;

    private Integer delayHours;

    private String reportedPort;

    private String remarks;

    private LocalDate reportedDate;
}