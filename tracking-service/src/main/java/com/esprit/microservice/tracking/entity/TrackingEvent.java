package com.esprit.microservice.tracking.entity;

import com.esprit.microservice.tracking.enums.PackageStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * Represents a single GPS location event for a delivery.
 * Stored in MongoDB collection "tracking_events".
 */
@Document(collection = "tracking_events")
@CompoundIndex(name = "delivery_time_idx", def = "{'deliveryId': 1, 'timestamp': -1}")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackingEvent {

    @Id
    private String id;

    @Indexed
    private String deliveryId;

    /** Latitude in decimal degrees */
    private double latitude;

    /** Longitude in decimal degrees */
    private double longitude;

    /** Speed in km/h (optional, sent by driver device) */
    private Double speed;

    /** Heading in degrees (0–360, 0 = North) */
    private Double heading;

    /** Current package status at the time of this event */
    private PackageStatus status;

    /** Optional notes (e.g., "Arrived at sorting facility") */
    private String notes;

    private Instant timestamp;
}
