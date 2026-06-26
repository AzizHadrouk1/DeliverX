package com.esprit.microservice.assignment.controller;


import com.esprit.microservice.assignment.model.Assignment;
import com.esprit.microservice.assignment.service.AssignmentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assignments")
@CrossOrigin("*")
public class AssignmentController {

    private final AssignmentService service;

    public AssignmentController(AssignmentService service) {
        this.service = service;
    }

    @GetMapping
    public List<Assignment> getAllAssignments() {
        return service.getAllAssignments();
    }

    @GetMapping("/{id}")
    public Assignment getAssignmentById(@PathVariable Long id) {
        return service.getAssignmentById(id);
    }

    @PostMapping
    public Assignment createAssignment(
            @RequestBody Assignment assignment) {

        return service.createAssignment(assignment);
    }

    @PutMapping("/{id}")
    public Assignment updateAssignment(
            @PathVariable Long id,
            @RequestBody Assignment assignment) {

        return service.updateAssignment(id, assignment);
    }

    @DeleteMapping("/{id}")
    public void deleteAssignment(@PathVariable Long id) {
        service.deleteAssignment(id);
    }

    @GetMapping("/driver/{driverId}")
    public List<Assignment> getAssignmentsByDriver(
            @PathVariable Long driverId) {

        return service.getAssignmentsByDriver(driverId);
    }

    @GetMapping("/delivery/{deliveryId}")
    public List<Assignment> getAssignmentsByDelivery(
            @PathVariable Long deliveryId) {

        return service.getAssignmentsByDelivery(deliveryId);
    }

    @GetMapping("/status/{status}")
    public List<Assignment> getAssignmentsByStatus(
            @PathVariable String status) {

        return service.getAssignmentsByStatus(status);
    }
}
