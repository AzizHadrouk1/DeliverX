package com.esprit.microservice.vehicle.service;

import com.esprit.microservice.vehicle.model.Vehicle;
import com.esprit.microservice.vehicle.model.VehicleStatus;
import com.esprit.microservice.vehicle.repository.VehicleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @Transactional(readOnly = true)
    public List<Vehicle> findAll() {
        return vehicleRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Vehicle findById(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Vehicle not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<Vehicle> findByStatus(VehicleStatus status) {
        return vehicleRepository.findByStatus(status);
    }

    public Vehicle create(Vehicle vehicle) {
        if (vehicleRepository.existsByLicensePlate(vehicle.getLicensePlate())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Vehicle with license plate already exists: " + vehicle.getLicensePlate());
        }
        return vehicleRepository.save(vehicle);
    }

    public Vehicle update(Long id, Vehicle updatedVehicle) {
        Vehicle existing = findById(id);

        if (!existing.getLicensePlate().equals(updatedVehicle.getLicensePlate())
                && vehicleRepository.existsByLicensePlate(updatedVehicle.getLicensePlate())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Vehicle with license plate already exists: " + updatedVehicle.getLicensePlate());
        }

        existing.setLicensePlate(updatedVehicle.getLicensePlate());
        existing.setBrand(updatedVehicle.getBrand());
        existing.setModel(updatedVehicle.getModel());
        existing.setType(updatedVehicle.getType());
        existing.setStatus(updatedVehicle.getStatus());
        existing.setMaxWeightCapacity(updatedVehicle.getMaxWeightCapacity());
        existing.setMaxVolumeCapacity(updatedVehicle.getMaxVolumeCapacity());
        existing.setManufacturingYear(updatedVehicle.getManufacturingYear());

        return vehicleRepository.save(existing);
    }

    public void delete(Long id) {
        if (!vehicleRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle not found: " + id);
        }
        vehicleRepository.deleteById(id);
    }
}
