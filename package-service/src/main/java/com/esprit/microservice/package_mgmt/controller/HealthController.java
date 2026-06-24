package com.esprit.microservice.package_mgmt.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthController {

    @GetMapping({"/health", "/packages/health"})
    public Map<String, String> health() {
        return Map.of(
                "status", "UP",
                "service", "PACKAGE-SERVICE"
        );
    }

    @GetMapping("/hello")
    public Map<String, String> hello() {
        return Map.of("message", "Hello from Package Management Service");
    }
}
