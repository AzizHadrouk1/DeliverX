package com.esprit.microservice.assignment.service;


import com.esprit.microservice.assignment.model.Assignment;
import com.esprit.microservice.assignment.repository.AssignmentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AssignmentService {

    private final AssignmentRepository repository;

    public AssignmentService(AssignmentRepository repository) {
        this.repository = repository;
    }

    public List<Assignment> getAllAssignments() {
        return repository.findAll();
    }

    public Assignment getAssignmentById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Assignment not found"));
    }

    public Assignment createAssignment(Assignment assignment) {

        assignment.setStatus("ASSIGNED");
        assignment.setAssignedAt(LocalDateTime.now());

        return repository.save(assignment);
    }

    public Assignment updateAssignment(Long id, Assignment updatedAssignment) {

        Assignment assignment = getAssignmentById(id);

        assignment.setDeliveryId(updatedAssignment.getDeliveryId());
        assignment.setDriverId(updatedAssignment.getDriverId());
        assignment.setVehicleId(updatedAssignment.getVehicleId());
        assignment.setStatus(updatedAssignment.getStatus());

        return repository.save(assignment);
    }

    public void deleteAssignment(Long id) {

        Assignment assignment = getAssignmentById(id);

        repository.delete(assignment);
    }

    public List<Assignment> getAssignmentsByDriver(Long driverId) {
        return repository.findByDriverId(driverId);
    }

    public List<Assignment> getAssignmentsByDelivery(Long deliveryId) {
        return repository.findByDeliveryId(deliveryId);
    }

    public List<Assignment> getAssignmentsByStatus(String status) {
        return repository.findByStatus(status);
    }
}
