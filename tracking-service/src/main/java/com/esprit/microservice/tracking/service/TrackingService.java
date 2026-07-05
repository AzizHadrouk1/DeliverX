package com.esprit.microservice.tracking.service;

import com.esprit.microservice.tracking.dto.LiveTrackingMessage;
import com.esprit.microservice.tracking.dto.LocationUpdateRequest;
import com.esprit.microservice.tracking.dto.StatusUpdateRequest;
import com.esprit.microservice.tracking.dto.TrackingEventResponse;
import com.esprit.microservice.tracking.entity.TrackingEvent;
import com.esprit.microservice.tracking.enums.PackageStatus;
import com.esprit.microservice.tracking.repository.TrackingEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrackingService {

    private final TrackingEventRepository trackingEventRepository;
    private final SimpMessagingTemplate messagingTemplate;

    // -----------------------------------------------------------------------
    // Location Updates
    // -----------------------------------------------------------------------

    /**
     * Persists a GPS location update and broadcasts it over WebSocket.
     *
     * @return the LiveTrackingMessage that was (or will be) broadcast
     */
    public LiveTrackingMessage processLocationUpdate(LocationUpdateRequest request) {
        // Determine current status (inherit from last event if not specified)
        PackageStatus currentStatus = resolveCurrentStatus(request.getDeliveryId());

        TrackingEvent event = TrackingEvent.builder()
                .deliveryId(request.getDeliveryId())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .speed(request.getSpeed())
                .heading(request.getHeading())
                .status(currentStatus)
                .notes(request.getNotes())
                .timestamp(Instant.now())
                .build();

        trackingEventRepository.save(event);
        log.debug("Saved tracking event for delivery={}", request.getDeliveryId());

        LiveTrackingMessage message = LiveTrackingMessage.builder()
                .eventType("LOCATION_UPDATE")
                .deliveryId(request.getDeliveryId())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .speed(request.getSpeed())
                .heading(request.getHeading())
                .status(currentStatus)
                .notes(request.getNotes())
                .timestamp(event.getTimestamp())
                .build();

        // Broadcast over WebSocket
        String topic = "/topic/tracking/" + request.getDeliveryId();
        messagingTemplate.convertAndSend(topic, message);

        return message;
    }

    // -----------------------------------------------------------------------
    // Status Updates
    // -----------------------------------------------------------------------

    /**
     * Records a package status change and broadcasts it over WebSocket.
     */
    public TrackingEventResponse updateStatus(String deliveryId, StatusUpdateRequest request) {
        // Get last known position (if any)
        Optional<TrackingEvent> lastEvent =
                trackingEventRepository.findFirstByDeliveryIdOrderByTimestampDesc(deliveryId);

        double lat = lastEvent.map(TrackingEvent::getLatitude).orElse(0.0);
        double lng = lastEvent.map(TrackingEvent::getLongitude).orElse(0.0);

        TrackingEvent event = TrackingEvent.builder()
                .deliveryId(deliveryId)
                .latitude(lat)
                .longitude(lng)
                .status(request.getStatus())
                .notes(request.getNotes())
                .timestamp(Instant.now())
                .build();

        trackingEventRepository.save(event);

        // Broadcast status update
        LiveTrackingMessage message = LiveTrackingMessage.builder()
                .eventType("STATUS_UPDATE")
                .deliveryId(deliveryId)
                .latitude(lat)
                .longitude(lng)
                .status(request.getStatus())
                .notes(request.getNotes())
                .timestamp(event.getTimestamp())
                .build();

        messagingTemplate.convertAndSend("/topic/tracking/" + deliveryId, message);

        return toResponse(event);
    }

    // -----------------------------------------------------------------------
    // Queries
    // -----------------------------------------------------------------------

    /**
     * Returns the full event history for a delivery (newest first).
     */
    public List<TrackingEventResponse> getHistory(String deliveryId) {
        return trackingEventRepository
                .findByDeliveryIdOrderByTimestampDesc(deliveryId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Returns the most recent tracking event for a delivery.
     */
    public Optional<TrackingEventResponse> getLatestLocation(String deliveryId) {
        return trackingEventRepository
                .findFirstByDeliveryIdOrderByTimestampDesc(deliveryId)
                .map(this::toResponse);
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private PackageStatus resolveCurrentStatus(String deliveryId) {
        return trackingEventRepository
                .findFirstByDeliveryIdOrderByTimestampDesc(deliveryId)
                .map(TrackingEvent::getStatus)
                .orElse(PackageStatus.IN_TRANSIT);
    }

    private TrackingEventResponse toResponse(TrackingEvent event) {
        return TrackingEventResponse.builder()
                .id(event.getId())
                .deliveryId(event.getDeliveryId())
                .latitude(event.getLatitude())
                .longitude(event.getLongitude())
                .speed(event.getSpeed())
                .heading(event.getHeading())
                .status(event.getStatus())
                .notes(event.getNotes())
                .timestamp(event.getTimestamp())
                .build();
    }
}
