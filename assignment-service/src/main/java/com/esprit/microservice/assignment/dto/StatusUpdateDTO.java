package com.esprit.microservice.assignment.dto;

import com.esprit.microservice.assignment.model.AssignmentStatus;
import jakarta.validation.constraints.NotNull;

public class StatusUpdateDTO {

    @NotNull
    private AssignmentStatus status;

    public AssignmentStatus getStatus() {
        return status;
    }

    public void setStatus(AssignmentStatus status) {
        this.status = status;
    }
}
