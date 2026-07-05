package com.esprit.microservice.tracking.dto;

import com.esprit.microservice.tracking.enums.PackageStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Message broadcast over WebSocket to all subscribers of a delivery's tracking topic.
 * Topic: /topic/tracking/{deliveryId}
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiveTrackingMessage {

    /** Type of event: LOCATION_UPDATE | STATUS_UPDATE | ETA_UPDATE */
    private String eventType;

    private String deliveryId;
    private double latitude;
    private double longitude;
    private Double speed;
    private Double heading;
    private PackageStatus status;
    private String notes;

    /** Current ETA in minutes (recalculated on each location update if destination is known) */
    private Double etaMinutes;

    private Instant timestamp;
}
