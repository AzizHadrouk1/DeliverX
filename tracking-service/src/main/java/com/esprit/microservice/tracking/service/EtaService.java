package com.esprit.microservice.tracking.service;

import com.esprit.microservice.tracking.dto.EtaResponse;
import com.esprit.microservice.tracking.entity.TrackingEvent;
import com.esprit.microservice.tracking.repository.TrackingEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * ETA calculation service using the Haversine formula.
 *
 * Formula:
 *   a = sin²(Δlat/2) + cos(lat1) * cos(lat2) * sin²(Δlng/2)
 *   c = 2 * atan2(√a, √(1−a))
 *   distance = EARTH_RADIUS_KM * c
 *
 * ETA (minutes) = (distance / speed) * 60
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EtaService {

    private static final double EARTH_RADIUS_KM = 6371.0;

    /** Default speed in km/h used when the driver hasn't reported speed */
    private static final double DEFAULT_SPEED_KMH = 40.0;

    private final TrackingEventRepository trackingEventRepository;

    /**
     * Calculates ETA from the driver's current position (latest tracking event) to a destination.
     *
     * @param deliveryId     the delivery ID
     * @param destLat        destination latitude
     * @param destLng        destination longitude
     * @param overrideSpeed  optional speed override in km/h (pass null to use last reported speed)
     */
    public EtaResponse calculateEta(String deliveryId, double destLat, double destLng,
                                    Double overrideSpeed) {
        TrackingEvent latest = trackingEventRepository
                .findFirstByDeliveryIdOrderByTimestampDesc(deliveryId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No tracking data found for delivery: " + deliveryId));

        double currentLat = latest.getLatitude();
        double currentLng = latest.getLongitude();

        double distanceKm = haversine(currentLat, currentLng, destLat, destLng);

        // Determine effective speed
        double speedKmh = DEFAULT_SPEED_KMH;
        if (overrideSpeed != null && overrideSpeed > 0) {
            speedKmh = overrideSpeed;
        } else if (latest.getSpeed() != null && latest.getSpeed() > 0) {
            speedKmh = latest.getSpeed();
        }

        double etaMinutes = (distanceKm / speedKmh) * 60.0;
        Instant estimatedArrival = Instant.now().plus((long) etaMinutes, ChronoUnit.MINUTES);

        log.info("ETA calc: delivery={} dist={}km speed={}km/h eta={}min",
                deliveryId, String.format("%.2f", distanceKm),
                String.format("%.1f", speedKmh), String.format("%.1f", etaMinutes));

        return EtaResponse.builder()
                .deliveryId(deliveryId)
                .etaMinutes(etaMinutes)
                .distanceKm(distanceKm)
                .currentLatitude(currentLat)
                .currentLongitude(currentLng)
                .destinationLatitude(destLat)
                .destinationLongitude(destLng)
                .speedKmh(speedKmh)
                .estimatedArrival(estimatedArrival)
                .build();
    }

    /**
     * Haversine formula — returns distance in km between two GPS coordinates.
     */
    public static double haversine(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }
}
