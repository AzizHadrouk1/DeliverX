package com.esprit.microservice.assignment.dto;

public record VehicleDTO(
        Long id,
        String licensePlate,
        String brand,
        String model,
        String status
) {
}
