package com.esprit.microservice.assignment.dto;

public record DeliveryDTO(
        Long id,
        Long packageId,
        String pickupAddress,
        String deliveryAddress,
        String status
) {
}
