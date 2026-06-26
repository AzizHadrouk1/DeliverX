package com.esprit.microservice.driverclient.controller;

import com.esprit.microservice.driverclient.dto.PageResponse;
import com.esprit.microservice.driverclient.model.Driver;
import com.esprit.microservice.driverclient.model.DriverStatus;
import com.esprit.microservice.driverclient.service.DriverService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/drivers")
public class DriverController {

    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    @GetMapping
    public Object getAllDrivers(
            @RequestParam(required = false) DriverStatus status,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Integer page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {

        if (page != null || (q != null && !q.isBlank())) {
            return driverService.search(q, status, page != null ? page : 0, size, sortBy, direction);
        }
        if (status != null) {
            return driverService.findByStatus(status);
        }
        return driverService.findAll();
    }

    @GetMapping("/search")
    public PageResponse<Driver> searchDrivers(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) DriverStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {
        return driverService.search(q, status, page, size, sortBy, direction);
    }

    @GetMapping("/{id}")
    public Driver getDriver(@PathVariable Long id) {
        return driverService.findById(id);
    }

    @PostMapping({"/create", ""})
    @ResponseStatus(HttpStatus.CREATED)
    public Driver createDriver(@Valid @RequestBody Driver driver) {
        return driverService.create(driver);
    }

    @PutMapping("/{id}")
    public Driver updateDriver(@PathVariable Long id, @Valid @RequestBody Driver driver) {
        return driverService.update(id, driver);
    }

    @PostMapping("/{id}/status")
    public Driver updateStatus(@PathVariable Long id, @RequestBody Map<String, DriverStatus> body) {
        return driverService.updateStatus(id, body.get("status"));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDriver(@PathVariable Long id) {
        driverService.delete(id);
    }
}
