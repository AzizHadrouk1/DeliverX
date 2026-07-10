package com.esprit.microservice.delivery.validation;

import com.esprit.microservice.delivery.enums.DeliveryStatus;
import com.esprit.microservice.delivery.exception.InvalidStatusTransitionException;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

@Component
public class StatusTransitionValidator {

    private static final Map<DeliveryStatus, Set<DeliveryStatus>> ALLOWED_TRANSITIONS = Map.of(
            DeliveryStatus.PENDING, EnumSet.of(DeliveryStatus.ASSIGNED, DeliveryStatus.CANCELLED),
            DeliveryStatus.ASSIGNED, EnumSet.of(DeliveryStatus.PICKED_UP, DeliveryStatus.CANCELLED),
            DeliveryStatus.PICKED_UP, EnumSet.of(DeliveryStatus.IN_PROGRESS, DeliveryStatus.CANCELLED),
            DeliveryStatus.IN_PROGRESS, EnumSet.of(DeliveryStatus.DELIVERED, DeliveryStatus.FAILED, DeliveryStatus.CANCELLED)
    );

    private static final Set<DeliveryStatus> TERMINAL_STATUSES = EnumSet.of(
            DeliveryStatus.DELIVERED,
            DeliveryStatus.FAILED,
            DeliveryStatus.CANCELLED
    );

    public void validate(DeliveryStatus current, DeliveryStatus target) {
        if (current == target) {
            throw new InvalidStatusTransitionException(current, target);
        }
        if (TERMINAL_STATUSES.contains(current)) {
            throw new InvalidStatusTransitionException(current, target);
        }
        Set<DeliveryStatus> allowed = ALLOWED_TRANSITIONS.get(current);
        if (allowed == null || !allowed.contains(target)) {
            throw new InvalidStatusTransitionException(current, target);
        }
    }

    public boolean isTerminal(DeliveryStatus status) {
        return TERMINAL_STATUSES.contains(status);
    }
}
