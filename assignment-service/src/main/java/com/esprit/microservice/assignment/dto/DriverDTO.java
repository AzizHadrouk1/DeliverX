package com.esprit.microservice.assignment.dto;

public record DriverDTO(
        Long id,
        String firstName,
        String lastName,
        String email,
        String status
) {
}
