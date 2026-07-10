package com.esprit.microservice.assignment.exception;

import com.esprit.microservice.assignment.model.AssignmentStatus;

public class InvalidStatusTransitionException extends RuntimeException {

    public InvalidStatusTransitionException(AssignmentStatus from, AssignmentStatus to) {
        super("Invalid status transition from " + from + " to " + to);
    }
}
