package com.esprit.microservice.delivery.dto;

public record PackageDTO(
        Long id,
        String trackingNumber,
        double weight,
        String destination,
        String status
) {
}
