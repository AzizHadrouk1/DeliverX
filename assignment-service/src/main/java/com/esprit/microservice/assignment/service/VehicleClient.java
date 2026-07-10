package com.esprit.microservice.assignment.service;

import com.esprit.microservice.assignment.dto.VehicleDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "VEHICLE-SERVICE")
public interface VehicleClient {

    @GetMapping("/vehicles/{id}")
    VehicleDTO getVehicle(@PathVariable("id") Long id);
}
