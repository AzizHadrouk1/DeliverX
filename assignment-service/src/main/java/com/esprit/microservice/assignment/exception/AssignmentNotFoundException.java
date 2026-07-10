package com.esprit.microservice.assignment.exception;

public class AssignmentNotFoundException extends RuntimeException {

    public AssignmentNotFoundException(Long id) {
        super("Assignment not found: " + id);
    }
}
