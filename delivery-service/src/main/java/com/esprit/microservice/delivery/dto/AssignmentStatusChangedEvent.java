package com.esprit.microservice.delivery.dto;

import java.time.LocalDateTime;

public record AssignmentStatusChangedEvent(
        Long assignmentId,
        Long deliveryId,
        String previousStatus,
        String newStatus,
        LocalDateTime changedAt
) {
}
