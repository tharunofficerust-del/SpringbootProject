package com.tharun.vessel_risk.dto;

import java.time.LocalDate;

import com.tharun.vessel_risk.enums.DelayReason;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateDelayReportRequest {

    @NotBlank(message = "Voyage number cannot be blank")
    private String voyageNumber;

    @NotNull(message = "Delay reason is required")
    private DelayReason delayReason;

    @NotNull(message = "Delay hours are required")
    @Positive(message = "Delay hours must be greater than zero")
    private Integer delayHours;

    @NotBlank(message = "Reported port cannot be blank")
    private String reportedPort;

    @NotBlank(message = "Remarks cannot be blank")
    private String remarks;

    @NotNull(message = "Reported date is required")
    private LocalDate reportedDate;
}