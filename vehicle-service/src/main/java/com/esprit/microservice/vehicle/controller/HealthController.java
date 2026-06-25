package com.esprit.microservice.vehicle.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RefreshScope
public class HealthController {

    @Value("${welcome.message:Welcome to Vehicle MS}")
    private String welcomeMessage;

    @GetMapping({"/health", "/vehicles/health"})
    public Map<String, String> health() {
        return Map.of(
                "status", "UP",
                "service", "VEHICLE-SERVICE"
        );
    }

    @GetMapping("/hello")
    public Map<String, String> hello() {
        return Map.of("message", "Hello from Vehicle Service");
    }

    @GetMapping("/welcome")
    public String welcome() {
        return welcomeMessage;
    }
}
