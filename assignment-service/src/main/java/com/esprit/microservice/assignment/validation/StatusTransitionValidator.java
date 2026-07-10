package com.esprit.microservice.assignment.validation;

import com.esprit.microservice.assignment.exception.InvalidStatusTransitionException;
import com.esprit.microservice.assignment.model.AssignmentStatus;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

@Component
public class StatusTransitionValidator {

    private static final Map<AssignmentStatus, Set<AssignmentStatus>> ALLOWED_TRANSITIONS = Map.of(
            AssignmentStatus.ASSIGNED, EnumSet.of(AssignmentStatus.IN_PROGRESS, AssignmentStatus.CANCELLED),
            AssignmentStatus.IN_PROGRESS, EnumSet.of(AssignmentStatus.COMPLETED, AssignmentStatus.CANCELLED)
    );

    private static final Set<AssignmentStatus> TERMINAL_STATUSES = EnumSet.of(
            AssignmentStatus.COMPLETED,
            AssignmentStatus.CANCELLED
    );

    public void validate(AssignmentStatus current, AssignmentStatus target) {
        if (current == target) {
            throw new InvalidStatusTransitionException(current, target);
        }
        if (TERMINAL_STATUSES.contains(current)) {
            throw new InvalidStatusTransitionException(current, target);
        }
        Set<AssignmentStatus> allowed = ALLOWED_TRANSITIONS.get(current);
        if (allowed == null || !allowed.contains(target)) {
            throw new InvalidStatusTransitionException(current, target);
        }
    }

    public boolean isTerminal(AssignmentStatus status) {
        return TERMINAL_STATUSES.contains(status);
    }
}
