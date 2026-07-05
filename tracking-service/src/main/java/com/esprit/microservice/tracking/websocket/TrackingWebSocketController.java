package com.esprit.microservice.tracking.websocket;

import com.esprit.microservice.tracking.dto.LiveTrackingMessage;
import com.esprit.microservice.tracking.dto.LocationUpdateRequest;
import com.esprit.microservice.tracking.service.TrackingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/**
 * WebSocket STOMP message controller.
 *
 * Driver devices send location updates via:
 *   SEND  /app/tracking.location
 *
 * All subscribers of /topic/tracking/{deliveryId} receive the broadcast.
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class TrackingWebSocketController {

    private final TrackingService trackingService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Receives a location push from the driver's device over WebSocket STOMP.
     * Persists to MongoDB and broadcasts to all subscribers of the delivery topic.
     */
    @MessageMapping("/tracking.location")
    public void handleLocationUpdate(@Payload LocationUpdateRequest request) {
        log.info("[WS] Location update from driver for delivery={} lat={} lng={}",
                request.getDeliveryId(), request.getLatitude(), request.getLongitude());

        LiveTrackingMessage message = trackingService.processLocationUpdate(request);

        // Broadcast to all clients subscribed to this delivery's topic
        String destination = "/topic/tracking/" + request.getDeliveryId();
        messagingTemplate.convertAndSend(destination, message);

        log.debug("[WS] Broadcast to {} -> {}", destination, message);
    }
}
