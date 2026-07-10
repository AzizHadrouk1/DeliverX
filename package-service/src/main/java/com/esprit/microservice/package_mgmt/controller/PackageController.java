package com.esprit.microservice.package_mgmt.controller;

import com.esprit.microservice.package_mgmt.model.PackageDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.Map;

@RestController
@RequestMapping("/packages")
public class PackageController {

    private static final Map<Long, PackageDTO> PACKAGES = Map.of(
            1L, new PackageDTO(1L, "DX-TRK-001", 2.5, "Tunis", "READY"),
            2L, new PackageDTO(2L, "DX-TRK-002", 5.0, "Sfax", "IN_TRANSIT"),
            3L, new PackageDTO(3L, "DX-TRK-003", 1.2, "Sousse", "DELIVERED")
    );

    @GetMapping
    public Collection<PackageDTO> getAllPackages() {
        return PACKAGES.values();
    }

    @GetMapping("/{id}")
    public PackageDTO getPackage(@PathVariable Long id) {
        PackageDTO pkg = PACKAGES.get(id);
        if (pkg == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Package not found: " + id);
        }
        return pkg;
    }
}
