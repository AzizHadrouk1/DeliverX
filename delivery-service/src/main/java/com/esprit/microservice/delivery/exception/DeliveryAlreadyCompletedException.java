package com.esprit.microservice.delivery.exception;

public class DeliveryAlreadyCompletedException extends RuntimeException {

    public DeliveryAlreadyCompletedException(Long id) {
        super("Delivery already completed or cancelled: " + id);
    }
}
