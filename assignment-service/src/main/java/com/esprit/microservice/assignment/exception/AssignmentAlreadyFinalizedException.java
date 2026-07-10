package com.esprit.microservice.assignment.exception;

public class AssignmentAlreadyFinalizedException extends RuntimeException {

    public AssignmentAlreadyFinalizedException(Long id) {
        super("Assignment already completed or cancelled: " + id);
    }
}
