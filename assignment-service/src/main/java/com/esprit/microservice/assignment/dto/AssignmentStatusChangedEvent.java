package com.esprit.microservice.assignment.dto;

import java.time.LocalDateTime;

public record AssignmentStatusChangedEvent(
        Long assignmentId,
        Long deliveryId,
        String previousStatus,
        String newStatus,
        LocalDateTime changedAt
) {
}
