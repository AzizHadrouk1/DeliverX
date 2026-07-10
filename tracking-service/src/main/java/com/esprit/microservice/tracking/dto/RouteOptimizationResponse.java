package com.esprit.microservice.tracking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteOptimizationResponse {
    private String deliveryId;

    /** Waypoints reordered by the nearest-neighbor algorithm */
    private List<OptimizedWaypoint> optimizedWaypoints;

    /** Total route distance in km */
    private double totalDistanceKm;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OptimizedWaypoint {
        private int order;
        private double latitude;
        private double longitude;
        private String label;
        /** Distance from previous waypoint in km */
        private double distanceFromPreviousKm;
    }
}
