package com.esprit.microservice.delivery.dto;

import java.time.LocalDateTime;

public record AssignmentCreatedEvent(
        Long assignmentId,
        Long deliveryId,
        Long driverId,
        Long vehicleId,
        LocalDateTime assignedAt
) {
}
