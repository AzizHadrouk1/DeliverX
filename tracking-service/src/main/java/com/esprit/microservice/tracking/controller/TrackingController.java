package com.esprit.microservice.tracking.controller;

import com.esprit.microservice.tracking.dto.*;
import com.esprit.microservice.tracking.service.EtaService;
import com.esprit.microservice.tracking.service.RouteOptimizationService;
import com.esprit.microservice.tracking.service.TrackingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API for the tracking service.
 *
 * Base path: /api/tracking
 *
 * Endpoints:
 *   POST   /api/tracking/{deliveryId}/location         — Push GPS location (REST alternative to WebSocket)
 *   PATCH  /api/tracking/{deliveryId}/status           — Update package status
 *   GET    /api/tracking/{deliveryId}/location         — Get latest GPS position
 *   GET    /api/tracking/{deliveryId}/history          — Get full event history
 *   GET    /api/tracking/{deliveryId}/eta              — Calculate ETA to destination
 *   POST   /api/tracking/{deliveryId}/route/optimize   — Optimize route waypoints
 *   GET    /api/tracking/{deliveryId}/route            — Get stored optimized route
 */
@RestController
@RequestMapping("/api/tracking")
@RequiredArgsConstructor
@Tag(name = "Tracking", description = "Real-time GPS tracking, ETA calculation, and route optimization")
public class TrackingController {

    private final TrackingService trackingService;
    private final EtaService etaService;
    private final RouteOptimizationService routeOptimizationService;

    // -------------------------------------------------------------------------
    // Location
    // -------------------------------------------------------------------------

    @PostMapping("/{deliveryId}/location")
    @Operation(summary = "Push GPS location update (REST)",
            description = "Persists the GPS fix and broadcasts it over WebSocket to all subscribers")
    public ResponseEntity<LiveTrackingMessage> pushLocation(
            @PathVariable String deliveryId,
            @Valid @RequestBody LocationUpdateRequest request) {
        request.setDeliveryId(deliveryId);
        LiveTrackingMessage message = trackingService.processLocationUpdate(request);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/{deliveryId}/location")
    @Operation(summary = "Get latest GPS position for a delivery")
    public ResponseEntity<TrackingEventResponse> getLatestLocation(@PathVariable String deliveryId) {
        return trackingService.getLatestLocation(deliveryId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // -------------------------------------------------------------------------
    // History
    // -------------------------------------------------------------------------

    @GetMapping("/{deliveryId}/history")
    @Operation(summary = "Get full GPS event history (newest first)")
    public ResponseEntity<List<TrackingEventResponse>> getHistory(@PathVariable String deliveryId) {
        return ResponseEntity.ok(trackingService.getHistory(deliveryId));
    }

    // -------------------------------------------------------------------------
    // Status
    // -------------------------------------------------------------------------

    @PatchMapping("/{deliveryId}/status")
    @Operation(summary = "Update package status during transport",
            description = "Records a status change event and broadcasts it over WebSocket")
    public ResponseEntity<TrackingEventResponse> updateStatus(
            @PathVariable String deliveryId,
            @Valid @RequestBody StatusUpdateRequest request) {
        return ResponseEntity.ok(trackingService.updateStatus(deliveryId, request));
    }

    // -------------------------------------------------------------------------
    // ETA
    // -------------------------------------------------------------------------

    @GetMapping("/{deliveryId}/eta")
    @Operation(summary = "Calculate ETA to destination using Haversine distance + current speed")
    public ResponseEntity<EtaResponse> calculateEta(
            @PathVariable String deliveryId,
            @Parameter(description = "Destination latitude") @RequestParam double destLat,
            @Parameter(description = "Destination longitude") @RequestParam double destLng,
            @Parameter(description = "Override speed in km/h (optional)") @RequestParam(required = false) Double speed) {
        return ResponseEntity.ok(etaService.calculateEta(deliveryId, destLat, destLng, speed));
    }

    // -------------------------------------------------------------------------
    // Route Optimization
    // -------------------------------------------------------------------------

    @PostMapping("/{deliveryId}/route/optimize")
    @Operation(summary = "Optimize delivery route using nearest-neighbor algorithm",
            description = "Reorders the provided waypoints to minimize total travel distance")
    public ResponseEntity<RouteOptimizationResponse> optimizeRoute(
            @PathVariable String deliveryId,
            @Valid @RequestBody RouteOptimizationRequest request) {
        request.setDeliveryId(deliveryId);
        return ResponseEntity.ok(routeOptimizationService.optimizeRoute(request));
    }

    @GetMapping("/{deliveryId}/route")
    @Operation(summary = "Get the stored optimized route for a delivery")
    public ResponseEntity<RouteOptimizationResponse> getRoute(@PathVariable String deliveryId) {
        return routeOptimizationService.getRoute(deliveryId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
