package com.esprit.microservice.assignment.controller;


import com.esprit.microservice.assignment.dto.AssignmentDTO;
import com.esprit.microservice.assignment.dto.AssignmentDetailsDTO;
import com.esprit.microservice.assignment.dto.AssignmentResponseDTO;
import com.esprit.microservice.assignment.dto.StatusUpdateDTO;
import com.esprit.microservice.assignment.model.AssignmentStatus;
import com.esprit.microservice.assignment.service.AssignmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {

    private final AssignmentService service;

    public AssignmentController(AssignmentService service) {
        this.service = service;
    }

    @GetMapping
    public List<AssignmentResponseDTO> getAllAssignments() {
        return service.getAllAssignments();
    }

    @GetMapping("/{id}")
    public AssignmentResponseDTO getAssignmentById(@PathVariable Long id) {
        return service.getAssignmentById(id);
    }

    @GetMapping("/{id}/details")
    public AssignmentDetailsDTO getAssignmentDetails(@PathVariable Long id) {
        return service.getAssignmentDetails(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AssignmentResponseDTO createAssignment(@Valid @RequestBody AssignmentDTO dto) {
        return service.createAssignment(dto);
    }

    @PutMapping("/{id}")
    public AssignmentResponseDTO updateAssignment(
            @PathVariable Long id,
            @Valid @RequestBody AssignmentDTO dto) {

        return service.updateAssignment(id, dto);
    }

    @PatchMapping("/{id}/status")
    public AssignmentResponseDTO updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusUpdateDTO statusUpdate) {

        return service.updateStatus(id, statusUpdate);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAssignment(@PathVariable Long id) {
        service.deleteAssignment(id);
    }

    @GetMapping("/driver/{driverId}")
    public List<AssignmentResponseDTO> getAssignmentsByDriver(
            @PathVariable Long driverId) {

        return service.getAssignmentsByDriver(driverId);
    }

    @GetMapping("/delivery/{deliveryId}")
    public List<AssignmentResponseDTO> getAssignmentsByDelivery(
            @PathVariable Long deliveryId) {

        return service.getAssignmentsByDelivery(deliveryId);
    }

    @GetMapping("/status/{status}")
    public List<AssignmentResponseDTO> getAssignmentsByStatus(
            @PathVariable AssignmentStatus status) {

        return service.getAssignmentsByStatus(status);
    }
}
