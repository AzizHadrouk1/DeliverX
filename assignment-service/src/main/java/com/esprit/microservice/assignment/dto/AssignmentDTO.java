package com.esprit.microservice.assignment.dto;

import jakarta.validation.constraints.NotNull;

public class AssignmentDTO {

    @NotNull
    private Long deliveryId;

    @NotNull
    private Long driverId;

    @NotNull
    private Long vehicleId;

    public Long getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(Long deliveryId) {
        this.deliveryId = deliveryId;
    }

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }
}
