package com.esprit.microservice.tracking.service;

import com.esprit.microservice.tracking.dto.RouteOptimizationRequest;
import com.esprit.microservice.tracking.dto.RouteOptimizationResponse;
import com.esprit.microservice.tracking.entity.DeliveryRoute;
import com.esprit.microservice.tracking.repository.DeliveryRouteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Route optimization service.
 *
 * Algorithm: Nearest-Neighbor (greedy TSP approximation).
 *   1. Start from the first waypoint (pickup / current position).
 *   2. Repeatedly visit the nearest unvisited waypoint.
 *   3. Result is a low-cost (not necessarily optimal) route computed in O(n²).
 *
 * For production, this can be replaced with OR-Tools or a routing API call.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RouteOptimizationService {

    private final DeliveryRouteRepository deliveryRouteRepository;

    /**
     * Optimizes the waypoints for a delivery using nearest-neighbor heuristic.
     * Persists the result to MongoDB and returns the optimized route.
     */
    public RouteOptimizationResponse optimizeRoute(RouteOptimizationRequest request) {
        List<RouteOptimizationRequest.WaypointRequest> input = request.getWaypoints();

        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException("At least one waypoint is required");
        }

        // Run nearest-neighbor TSP
        List<int[]> visitOrder = nearestNeighbor(input); // returns ordered indices

        // Build response
        List<RouteOptimizationResponse.OptimizedWaypoint> optimized = new ArrayList<>();
        double totalDistance = 0.0;
        double prevLat = 0, prevLng = 0;
        boolean first = true;

        for (int i = 0; i < visitOrder.size(); i++) {
            int idx = visitOrder.get(i)[0];
            RouteOptimizationRequest.WaypointRequest wp = input.get(idx);

            double distFromPrev = 0.0;
            if (!first) {
                distFromPrev = EtaService.haversine(prevLat, prevLng, wp.getLatitude(), wp.getLongitude());
                totalDistance += distFromPrev;
            }
            first = false;
            prevLat = wp.getLatitude();
            prevLng = wp.getLongitude();

            optimized.add(RouteOptimizationResponse.OptimizedWaypoint.builder()
                    .order(i)
                    .latitude(wp.getLatitude())
                    .longitude(wp.getLongitude())
                    .label(wp.getLabel() != null ? wp.getLabel() : "Stop " + i)
                    .distanceFromPreviousKm(distFromPrev)
                    .build());
        }

        // Persist
        persistRoute(request.getDeliveryId(), optimized, totalDistance);

        log.info("Route optimized for delivery={} waypoints={} totalKm={}",
                request.getDeliveryId(), optimized.size(), String.format("%.2f", totalDistance));

        return RouteOptimizationResponse.builder()
                .deliveryId(request.getDeliveryId())
                .optimizedWaypoints(optimized)
                .totalDistanceKm(totalDistance)
                .build();
    }

    /**
     * Retrieves the stored optimized route for a delivery.
     */
    public Optional<RouteOptimizationResponse> getRoute(String deliveryId) {
        return deliveryRouteRepository.findByDeliveryId(deliveryId)
                .map(this::toResponse);
    }

    // -----------------------------------------------------------------------
    // Nearest-Neighbor Algorithm
    // -----------------------------------------------------------------------

    /**
     * Greedy nearest-neighbor: always visit the closest unvisited node.
     * First waypoint (index 0) is always the starting point (pickup location).
     *
     * @return ordered list of [originalIndex] pairs
     */
    private List<int[]> nearestNeighbor(List<RouteOptimizationRequest.WaypointRequest> wps) {
        int n = wps.size();
        boolean[] visited = new boolean[n];
        List<int[]> order = new ArrayList<>();

        // Always start from index 0 (pickup / current location)
        int current = 0;
        visited[current] = true;
        order.add(new int[]{current});

        for (int step = 1; step < n; step++) {
            double minDist = Double.MAX_VALUE;
            int nearest = -1;

            RouteOptimizationRequest.WaypointRequest cur = wps.get(current);
            for (int j = 0; j < n; j++) {
                if (!visited[j]) {
                    RouteOptimizationRequest.WaypointRequest candidate = wps.get(j);
                    double d = EtaService.haversine(
                            cur.getLatitude(), cur.getLongitude(),
                            candidate.getLatitude(), candidate.getLongitude());
                    if (d < minDist) {
                        minDist = d;
                        nearest = j;
                    }
                }
            }

            visited[nearest] = true;
            order.add(new int[]{nearest});
            current = nearest;
        }

        return order;
    }

    // -----------------------------------------------------------------------
    // Persistence
    // -----------------------------------------------------------------------

    private void persistRoute(String deliveryId,
                              List<RouteOptimizationResponse.OptimizedWaypoint> optimized,
                              double totalDistance) {
        // Convert to entity waypoints
        List<DeliveryRoute.Waypoint> entityWaypoints = optimized.stream()
                .map(w -> DeliveryRoute.Waypoint.builder()
                        .latitude(w.getLatitude())
                        .longitude(w.getLongitude())
                        .label(w.getLabel())
                        .order(w.getOrder())
                        .build())
                .toList();

        Instant now = Instant.now();
        Optional<DeliveryRoute> existing = deliveryRouteRepository.findByDeliveryId(deliveryId);

        DeliveryRoute route = existing.map(r -> {
            r.setWaypoints(entityWaypoints);
            r.setTotalDistanceKm(totalDistance);
            r.setOptimized(true);
            r.setUpdatedAt(now);
            return r;
        }).orElse(DeliveryRoute.builder()
                .deliveryId(deliveryId)
                .waypoints(entityWaypoints)
                .totalDistanceKm(totalDistance)
                .optimized(true)
                .createdAt(now)
                .updatedAt(now)
                .build());

        deliveryRouteRepository.save(route);
    }

    private RouteOptimizationResponse toResponse(DeliveryRoute route) {
        List<RouteOptimizationResponse.OptimizedWaypoint> wps = route.getWaypoints()
                .stream()
                .map(w -> RouteOptimizationResponse.OptimizedWaypoint.builder()
                        .order(w.getOrder())
                        .latitude(w.getLatitude())
                        .longitude(w.getLongitude())
                        .label(w.getLabel())
                        .distanceFromPreviousKm(0.0)
                        .build())
                .toList();

        return RouteOptimizationResponse.builder()
                .deliveryId(route.getDeliveryId())
                .optimizedWaypoints(wps)
                .totalDistanceKm(route.getTotalDistanceKm() != null ? route.getTotalDistanceKm() : 0.0)
                .build();
    }
}
