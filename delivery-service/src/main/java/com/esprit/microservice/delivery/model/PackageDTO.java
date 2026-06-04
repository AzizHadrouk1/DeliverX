package com.esprit.microservice.delivery.model;

public record PackageDTO(
        Long id,
        String trackingNumber,
        double weight,
        String destination,
        String status
) {
}
