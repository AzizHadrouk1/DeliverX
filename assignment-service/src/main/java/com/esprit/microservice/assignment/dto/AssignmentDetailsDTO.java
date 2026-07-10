package com.esprit.microservice.assignment.dto;

public record AssignmentDetailsDTO(
        AssignmentResponseDTO assignment,
        DriverDTO driver,
        VehicleDTO vehicle,
        DeliveryDTO delivery
) {
}
