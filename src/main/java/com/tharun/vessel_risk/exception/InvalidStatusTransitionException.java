package com.tharun.vessel_risk.exception;

public class InvalidStatusTransitionException extends RuntimeException {

    public InvalidStatusTransitionException(String message) {
        super(message);
    }
}

// ARRIVED → IN_TRANSIT
// CANCELLED → DEPARTED
// DELIVERED → CREATED