package com.esprit.microservice.delivery.exception;

public class DeliveryNotFoundException extends RuntimeException {

    public DeliveryNotFoundException(Long id) {
        super("Delivery not found: " + id);
    }
}
