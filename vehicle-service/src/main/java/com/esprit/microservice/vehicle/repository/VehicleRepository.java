package com.esprit.microservice.vehicle.repository;

import com.esprit.microservice.vehicle.model.Vehicle;
import com.esprit.microservice.vehicle.model.VehicleStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    List<Vehicle> findByStatus(VehicleStatus status);

    boolean existsByLicensePlate(String licensePlate);
}
