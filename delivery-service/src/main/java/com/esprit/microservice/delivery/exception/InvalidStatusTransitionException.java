package com.esprit.microservice.delivery.exception;

import com.esprit.microservice.delivery.enums.DeliveryStatus;

public class InvalidStatusTransitionException extends RuntimeException {

    public InvalidStatusTransitionException(DeliveryStatus from, DeliveryStatus to) {
        super("Invalid status transition from " + from + " to " + to);
    }
}
