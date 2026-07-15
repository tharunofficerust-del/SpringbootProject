package com.tharun.vessel_risk.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateDelayRequest {

    @Min(value = 1, message = "Delay hours must be greater than 0")
    private Integer delayHours;

    @NotBlank(message = "Remarks cannot be empty")
    private String remarks;
}