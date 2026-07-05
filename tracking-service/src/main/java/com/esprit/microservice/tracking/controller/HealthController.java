package com.esprit.microservice.tracking.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of(
                "status", "UP",
                "service", "TRACKING-SERVICE"
        );
    }

    @GetMapping("/hello")
    public Map<String, String> hello() {
        return Map.of("message", "Hello from Tracking Service — Real-Time GPS & Route Optimization");
    }
}
