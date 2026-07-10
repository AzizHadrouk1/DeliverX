package com.esprit.microservice.assignment.client;

import com.esprit.microservice.assignment.dto.DriverDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "DRIVER-CLIENT-SERVICE")
public interface DriverClient {

    @GetMapping("/drivers/{id}")
    DriverDTO getDriver(@PathVariable("id") Long id);
}
