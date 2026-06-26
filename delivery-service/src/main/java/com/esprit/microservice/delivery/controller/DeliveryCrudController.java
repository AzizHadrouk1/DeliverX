package com.esprit.microservice.delivery.controller;

import com.esprit.microservice.delivery.dto.DeliveryDTO;
import com.esprit.microservice.delivery.dto.DeliveryProofDTO;
import com.esprit.microservice.delivery.dto.DeliveryProofResponseDTO;
import com.esprit.microservice.delivery.dto.DeliveryResponseDTO;
import com.esprit.microservice.delivery.dto.StatusUpdateDTO;
import com.esprit.microservice.delivery.enums.DeliveryStatus;
import com.esprit.microservice.delivery.service.DeliveryProofService;
import com.esprit.microservice.delivery.service.DeliveryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/deliveries")
public class DeliveryCrudController {

    private final DeliveryService deliveryService;
    private final DeliveryProofService deliveryProofService;

    public DeliveryCrudController(DeliveryService deliveryService, DeliveryProofService deliveryProofService) {
        this.deliveryService = deliveryService;
        this.deliveryProofService = deliveryProofService;
    }

    @GetMapping
    public Page<DeliveryResponseDTO> getAllDeliveries(
            Pageable pageable,
            @RequestParam(required = false) DeliveryStatus status,
            @RequestParam(required = false) Long driverId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return deliveryService.findAll(pageable, status, driverId, date);
    }

    @GetMapping("/schedule")
    public List<DeliveryResponseDTO> getScheduledDeliveries(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return deliveryService.findByScheduledDate(date);
    }

    @GetMapping("/driver/{driverId}")
    public List<DeliveryResponseDTO> getDeliveriesByDriver(@PathVariable Long driverId) {
        return deliveryService.findByDriverId(driverId);
    }

    @GetMapping("/{id}")
    public DeliveryResponseDTO getDelivery(@PathVariable Long id) {
        return deliveryService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DeliveryResponseDTO createDelivery(@Valid @RequestBody DeliveryDTO dto) {
        return deliveryService.create(dto);
    }

    @PutMapping("/{id}")
    public DeliveryResponseDTO updateDelivery(@PathVariable Long id, @Valid @RequestBody DeliveryDTO dto) {
        return deliveryService.update(id, dto);
    }

    @PatchMapping("/{id}/status")
    public DeliveryResponseDTO updateStatus(@PathVariable Long id, @Valid @RequestBody StatusUpdateDTO statusUpdate) {
        return deliveryService.updateStatus(id, statusUpdate);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDelivery(@PathVariable Long id) {
        deliveryService.deleteOrCancel(id);
    }

    @GetMapping("/{id}/proof")
    public DeliveryProofResponseDTO getProof(@PathVariable Long id) {
        return deliveryProofService.getProof(id);
    }

    @PostMapping("/{id}/proof")
    @ResponseStatus(HttpStatus.CREATED)
    public DeliveryProofResponseDTO createProof(@PathVariable Long id, @Valid @RequestBody DeliveryProofDTO dto) {
        return deliveryProofService.createProof(id, dto);
    }
}
