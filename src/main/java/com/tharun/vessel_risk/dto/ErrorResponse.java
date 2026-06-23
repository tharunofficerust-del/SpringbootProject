package com.tharun.vessel_risk.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse {

    private LocalDateTime timestamp;
    private int statusCode;
    private String error;
    private String message;
    private String path;
}