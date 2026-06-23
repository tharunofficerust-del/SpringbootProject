package com.tharun.vessel_risk.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}

// Voyage not found
// Shipment not found
// Delay report not found