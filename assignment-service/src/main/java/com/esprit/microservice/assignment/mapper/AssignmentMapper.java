package com.esprit.microservice.assignment.mapper;

import com.esprit.microservice.assignment.dto.AssignmentDTO;
import com.esprit.microservice.assignment.dto.AssignmentResponseDTO;
import com.esprit.microservice.assignment.model.Assignment;
import org.springframework.stereotype.Component;

@Component
public class AssignmentMapper {

    public Assignment toEntity(AssignmentDTO dto) {
        Assignment assignment = new Assignment();
        assignment.setDeliveryId(dto.getDeliveryId());
        assignment.setDriverId(dto.getDriverId());
        assignment.setVehicleId(dto.getVehicleId());
        return assignment;
    }

    public void updateEntity(Assignment assignment, AssignmentDTO dto) {
        assignment.setDeliveryId(dto.getDeliveryId());
        assignment.setDriverId(dto.getDriverId());
        assignment.setVehicleId(dto.getVehicleId());
    }

    public AssignmentResponseDTO toResponse(Assignment assignment) {
        AssignmentResponseDTO dto = new AssignmentResponseDTO();
        dto.setId(assignment.getId());
        dto.setDeliveryId(assignment.getDeliveryId());
        dto.setDriverId(assignment.getDriverId());
        dto.setVehicleId(assignment.getVehicleId());
        dto.setStatus(assignment.getStatus());
        dto.setAssignedAt(assignment.getAssignedAt());
        dto.setUpdatedAt(assignment.getUpdatedAt());
        return dto;
    }
}
