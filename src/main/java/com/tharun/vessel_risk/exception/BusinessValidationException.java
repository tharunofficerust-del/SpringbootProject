package com.tharun.vessel_risk.exception;

public class BusinessValidationException extends RuntimeException {

    public BusinessValidationException(String message) {
        super(message);
    }
}

// Origin and destination same

// HAZARDOUS + LOW priority

// Delay > 120 hours

// Required delivery date invalid