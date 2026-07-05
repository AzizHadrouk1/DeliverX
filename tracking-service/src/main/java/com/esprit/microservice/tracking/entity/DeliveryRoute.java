package com.esprit.microservice.tracking.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

/**
 * Stores the planned (and optionally optimized) route for a delivery.
 * Contains an ordered list of waypoints.
 */
@Document(collection = "delivery_routes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryRoute {

    @Id
    private String id;

    @Indexed(unique = true)
    private String deliveryId;

    /** Ordered list of waypoints for this delivery */
    private List<Waypoint> waypoints;

    /** Total distance in km (calculated after optimization) */
    private Double totalDistanceKm;

    /** Whether the route has been optimized by the service */
    private boolean optimized;

    private Instant createdAt;
    private Instant updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Waypoint {
        private double latitude;
        private double longitude;
        /** Human-readable label (e.g., "Pickup", "Warehouse A", "Customer") */
        private String label;
        /** Order index after optimization */
        private int order;
    }
}
