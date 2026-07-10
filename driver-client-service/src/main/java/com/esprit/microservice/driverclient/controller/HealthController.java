package com.esprit.microservice.driverclient.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RefreshScope
public class HealthController {

    @Value("${welcome.message:Welcome to Driver & Client MS}")
    private String welcomeMessage;

    @GetMapping({"/health", "/drivers/health", "/clients/health"})
    public Map<String, String> health() {
        return Map.of(
                "status", "UP",
                "service", "DRIVER-CLIENT-SERVICE"
        );
    }

    @GetMapping("/hello")
    public Map<String, String> hello() {
        return Map.of("message", "Hello from Driver & Client Service");
    }

    @GetMapping("/welcome")
    public String welcome() {
        return welcomeMessage;
    }
}
