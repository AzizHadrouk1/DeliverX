package com.esprit.microservice.driverclient.service;

import com.esprit.microservice.driverclient.dto.PageResponse;
import com.esprit.microservice.driverclient.model.Driver;
import com.esprit.microservice.driverclient.model.DriverStatus;
import com.esprit.microservice.driverclient.repository.DriverRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class DriverService {

    private final DriverRepository driverRepository;
    private final ActionLogService actionLogService;

    public DriverService(DriverRepository driverRepository, ActionLogService actionLogService) {
        this.driverRepository = driverRepository;
        this.actionLogService = actionLogService;
    }

    @Transactional(readOnly = true)
    public List<Driver> findAll() {
        return driverRepository.findAll();
    }

    @Transactional(readOnly = true)
    public PageResponse<Driver> search(String q, DriverStatus status, int page, int size, String sortBy, String direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sortBy));
        Page<Driver> result = driverRepository.search(q, status, pageable);
        return toPageResponse(result);
    }

    @Transactional(readOnly = true)
    public Driver findById(Long id) {
        return driverRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Driver not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<Driver> findByStatus(DriverStatus status) {
        return driverRepository.findByStatus(status);
    }

    public Driver create(Driver driver) {
        if (driverRepository.existsByEmail(driver.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Driver with email already exists: " + driver.getEmail());
        }
        if (driverRepository.existsByLicenseNumber(driver.getLicenseNumber())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Driver with license number already exists: " + driver.getLicenseNumber());
        }
        Driver saved = driverRepository.save(driver);
        actionLogService.log("system", "CREATE", "Driver", saved.getId(), saved.getEmail());
        return saved;
    }

    public Driver update(Long id, Driver updatedDriver) {
        Driver existing = findById(id);

        if (!existing.getEmail().equals(updatedDriver.getEmail())
                && driverRepository.existsByEmail(updatedDriver.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Driver with email already exists: " + updatedDriver.getEmail());
        }
        if (!existing.getLicenseNumber().equals(updatedDriver.getLicenseNumber())
                && driverRepository.existsByLicenseNumber(updatedDriver.getLicenseNumber())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Driver with license number already exists: " + updatedDriver.getLicenseNumber());
        }

        existing.setFirstName(updatedDriver.getFirstName());
        existing.setLastName(updatedDriver.getLastName());
        existing.setEmail(updatedDriver.getEmail());
        existing.setPhone(updatedDriver.getPhone());
        existing.setLicenseNumber(updatedDriver.getLicenseNumber());
        existing.setStatus(updatedDriver.getStatus());
        existing.setVehicleId(updatedDriver.getVehicleId());

        Driver saved = driverRepository.save(existing);
        actionLogService.log("system", "UPDATE", "Driver", saved.getId(), saved.getEmail());
        return saved;
    }

    public Driver updateStatus(Long id, DriverStatus status) {
        Driver driver = findById(id);
        driver.setStatus(status);
        Driver saved = driverRepository.save(driver);
        actionLogService.log("system", "STATUS_CHANGE", "Driver", saved.getId(), status.name());
        return saved;
    }

    public void delete(Long id) {
        if (!driverRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver not found: " + id);
        }
        driverRepository.deleteById(id);
        actionLogService.log("system", "DELETE", "Driver", id, null);
    }

    private PageResponse<Driver> toPageResponse(Page<Driver> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }
}
