package com.esprit.microservice.package_mgmt.controller;

<<<<<<< Updated upstream
import com.esprit.microservice.package_mgmt.model.PackageDTO;
import com.esprit.microservice.package_mgmt.service.PackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
=======
import com.esprit.microservice.package_mgmt.dto.PackageDTO;
import com.esprit.microservice.package_mgmt.dto.PackageResponseDTO;
import com.esprit.microservice.package_mgmt.dto.PackageStatusHistoryDTO;
import com.esprit.microservice.package_mgmt.dto.UpdatePackageStatusDTO;
import com.esprit.microservice.package_mgmt.enums.PackageStatus;
import com.esprit.microservice.package_mgmt.service.PackageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
>>>>>>> Stashed changes
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/packages")
<<<<<<< Updated upstream
@RequiredArgsConstructor
public class PackageController {

    private final PackageService packageService;

    @GetMapping
    public ResponseEntity<List<PackageDTO>> getAllPackages() {
        return ResponseEntity.ok(packageService.getAllPackages());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PackageDTO> getPackageById(@PathVariable Long id) {
        return ResponseEntity.ok(packageService.getPackageById(id));
    }

    @PostMapping
    public ResponseEntity<PackageDTO> createPackage(@RequestBody PackageDTO packageDTO) {
        PackageDTO created = packageService.createPackage(packageDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PackageDTO> updatePackage(
            @PathVariable Long id,
            @RequestBody PackageDTO packageDTO) {
        return ResponseEntity.ok(packageService.updatePackage(id, packageDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePackage(@PathVariable Long id) {
        packageService.deletePackage(id);
        return ResponseEntity.noContent().build();
=======
@Tag(name = "Packages", description = "Package lifecycle management")
public class PackageController {

    private final PackageService packageService;

    public PackageController(PackageService packageService) {
        this.packageService = packageService;
    }

    @GetMapping
    @Operation(summary = "List packages (paginated, filterable by status and clientId)")
    public Page<PackageResponseDTO> getAllPackages(
            Pageable pageable,
            @RequestParam(required = false) PackageStatus status,
            @RequestParam(required = false) Long clientId) {
        return packageService.findAll(pageable, status, clientId);
    }

    @GetMapping("/tracking/{trackingNumber}")
    @Operation(summary = "Get package by tracking number")
    public PackageResponseDTO getByTrackingNumber(@PathVariable String trackingNumber) {
        return packageService.findByTrackingNumber(trackingNumber);
    }

    @GetMapping("/client/{clientId}")
    @Operation(summary = "List packages for a client")
    public List<PackageResponseDTO> getByClientId(@PathVariable Long clientId) {
        return packageService.findByClientId(clientId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get package by ID")
    public PackageResponseDTO getPackage(@PathVariable Long id) {
        return packageService.findById(id);
    }

    @GetMapping("/{id}/history")
    @Operation(summary = "Get package status history")
    public List<PackageStatusHistoryDTO> getHistory(@PathVariable Long id) {
        return packageService.getHistory(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a package")
    public PackageResponseDTO createPackage(@Valid @RequestBody PackageDTO dto) {
        return packageService.create(dto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update package details")
    public PackageResponseDTO updatePackage(@PathVariable Long id, @Valid @RequestBody PackageDTO dto) {
        return packageService.update(id, dto);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update package status")
    public PackageResponseDTO updateStatus(@PathVariable Long id, @Valid @RequestBody UpdatePackageStatusDTO statusUpdate) {
        return packageService.updateStatus(id, statusUpdate);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete package (only when status is CREATED)")
    public void deletePackage(@PathVariable Long id) {
        packageService.delete(id);
>>>>>>> Stashed changes
    }
}
