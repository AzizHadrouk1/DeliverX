package com.esprit.microservice.delivery.listener;

import com.esprit.microservice.delivery.config.RabbitMQConfig;
import com.esprit.microservice.delivery.dto.AssignmentCreatedEvent;
import com.esprit.microservice.delivery.dto.AssignmentStatusChangedEvent;
import com.esprit.microservice.delivery.dto.StatusUpdateDTO;
import com.esprit.microservice.delivery.enums.DeliveryStatus;
import com.esprit.microservice.delivery.service.DeliveryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Consumes assignment events published by assignment-service over RabbitMQ and
 * keeps the delivery status in sync asynchronously.
 */
@Component
public class AssignmentEventListener {

    private static final Logger log = LoggerFactory.getLogger(AssignmentEventListener.class);

    /** Ordered forward chain of the delivery state machine. */
    private static final List<DeliveryStatus> FORWARD_CHAIN = List.of(
            DeliveryStatus.PENDING,
            DeliveryStatus.ASSIGNED,
            DeliveryStatus.PICKED_UP,
            DeliveryStatus.IN_PROGRESS,
            DeliveryStatus.DELIVERED
    );

    /** Assignment status -> delivery status the delivery should reach. */
    private static final Map<String, DeliveryStatus> TARGET_BY_ASSIGNMENT_STATUS = Map.of(
            "IN_PROGRESS", DeliveryStatus.IN_PROGRESS,
            "COMPLETED", DeliveryStatus.DELIVERED,
            "CANCELLED", DeliveryStatus.CANCELLED
    );

    private final DeliveryService deliveryService;

    public AssignmentEventListener(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @RabbitListener(queues = RabbitMQConfig.CREATED_QUEUE)
    public void onAssignmentCreated(AssignmentCreatedEvent event) {
        log.info("Received assignment.created event: assignment {} for delivery {}",
                event.assignmentId(), event.deliveryId());
        advanceDelivery(event.deliveryId(), DeliveryStatus.ASSIGNED,
                "Assigned by assignment " + event.assignmentId());
    }

    @RabbitListener(queues = RabbitMQConfig.STATUS_QUEUE)
    public void onAssignmentStatusChanged(AssignmentStatusChangedEvent event) {
        log.info("Received assignment.status.changed event: assignment {} {} -> {} (delivery {})",
                event.assignmentId(), event.previousStatus(), event.newStatus(), event.deliveryId());

        DeliveryStatus target = TARGET_BY_ASSIGNMENT_STATUS.get(event.newStatus());
        if (target == null) {
            log.debug("No delivery mapping for assignment status {}", event.newStatus());
            return;
        }
        advanceDelivery(event.deliveryId(), target,
                "Assignment " + event.assignmentId() + " changed to " + event.newStatus());
    }

    /**
     * Walks the delivery through the state machine one legal transition at a time
     * until it reaches the target (CANCELLED is reachable from any active state).
     */
    private void advanceDelivery(Long deliveryId, DeliveryStatus target, String note) {
        try {
            DeliveryStatus current = deliveryService.findById(deliveryId).getStatus();

            if (target == DeliveryStatus.CANCELLED) {
                if (current != DeliveryStatus.CANCELLED) {
                    applyStatus(deliveryId, DeliveryStatus.CANCELLED, note);
                }
                return;
            }

            int currentIdx = FORWARD_CHAIN.indexOf(current);
            int targetIdx = FORWARD_CHAIN.indexOf(target);
            if (currentIdx < 0 || targetIdx < 0 || currentIdx >= targetIdx) {
                log.warn("Delivery {} is {} — cannot advance to {}", deliveryId, current, target);
                return;
            }
            for (int i = currentIdx + 1; i <= targetIdx; i++) {
                applyStatus(deliveryId, FORWARD_CHAIN.get(i), note);
            }
        } catch (Exception e) {
            log.error("Failed to sync delivery {} to {}: {}", deliveryId, target, e.getMessage());
        }
    }

    private void applyStatus(Long deliveryId, DeliveryStatus status, String note) {
        StatusUpdateDTO update = new StatusUpdateDTO();
        update.setStatus(status);
        update.setNote(note);
        deliveryService.updateStatus(deliveryId, update);
        log.info("Delivery {} advanced to {}", deliveryId, status);
    }
}
