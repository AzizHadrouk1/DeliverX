package com.esprit.microservice.tracking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * ETA response including estimated arrival time, distance, and current position.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EtaResponse {
    private String deliveryId;

    /** Estimated time of arrival in minutes from now */
    private double etaMinutes;

    /** Remaining distance to destination in km */
    private double distanceKm;

    /** Current driver latitude */
    private double currentLatitude;

    /** Current driver longitude */
    private double currentLongitude;

    /** Destination latitude */
    private double destinationLatitude;

    /** Destination longitude */
    private double destinationLongitude;

    /** Current speed used for calculation (km/h) */
    private double speedKmh;

    /** Calculated ETA as an absolute timestamp */
    private Instant estimatedArrival;
}
