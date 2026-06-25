package com.esprit.microservice.vehicle.config;

import com.esprit.microservice.vehicle.model.Vehicle;
import com.esprit.microservice.vehicle.model.VehicleStatus;
import com.esprit.microservice.vehicle.model.VehicleType;
import com.esprit.microservice.vehicle.repository.VehicleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner seedVehicles(VehicleRepository vehicleRepository) {
        return args -> {
            if (vehicleRepository.count() > 0) {
                return;
            }

            vehicleRepository.save(buildVehicle(
                    "TN-1234-A", "Renault", "Master", VehicleType.VAN,
                    VehicleStatus.AVAILABLE, 1200.0, 10.5, 2022));

            vehicleRepository.save(buildVehicle(
                    "TN-5678-B", "Mercedes", "Sprinter", VehicleType.TRUCK,
                    VehicleStatus.IN_USE, 3500.0, 14.0, 2021));

            vehicleRepository.save(buildVehicle(
                    "TN-9012-C", "Yamaha", "NMAX", VehicleType.MOTORCYCLE,
                    VehicleStatus.MAINTENANCE, 15.0, 0.05, 2023));
        };
    }

    private Vehicle buildVehicle(String licensePlate, String brand, String model,
                                 VehicleType type, VehicleStatus status,
                                 Double maxWeightCapacity, Double maxVolumeCapacity,
                                 Integer manufacturingYear) {
        Vehicle vehicle = new Vehicle();
        vehicle.setLicensePlate(licensePlate);
        vehicle.setBrand(brand);
        vehicle.setModel(model);
        vehicle.setType(type);
        vehicle.setStatus(status);
        vehicle.setMaxWeightCapacity(maxWeightCapacity);
        vehicle.setMaxVolumeCapacity(maxVolumeCapacity);
        vehicle.setManufacturingYear(manufacturingYear);
        return vehicle;
    }
}
