package com.tharun.vessel_risk.dto;

import java.time.LocalDate;

import com.tharun.vessel_risk.enums.DelayReason;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DelayResponse {

    private Long id;

    private String voyageNumber;

    private DelayReason delayReason;

    private Integer delayHours;

    private String reportedPort;

    private String remarks;

    private LocalDate reportedDate;
}