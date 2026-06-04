package com.esprit.microservice.package_mgmt.model;

public record PackageDTO(
        Long id,
        String trackingNumber,
        double weight,
        String destination,
        String status
) {
}
