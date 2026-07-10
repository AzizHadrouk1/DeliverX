package com.esprit.microservice.tracking.dto;

import com.esprit.microservice.tracking.enums.PackageStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackingEventResponse {
    private String id;
    private String deliveryId;
    private double latitude;
    private double longitude;
    private Double speed;
    private Double heading;
    private PackageStatus status;
    private String notes;
    private Instant timestamp;
}
