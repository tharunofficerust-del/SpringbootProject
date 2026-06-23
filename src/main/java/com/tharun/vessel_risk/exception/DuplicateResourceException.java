package com.tharun.vessel_risk.exception;

public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}

// Duplicate voyage number
// Duplicate shipment reference
// Duplicate delay report