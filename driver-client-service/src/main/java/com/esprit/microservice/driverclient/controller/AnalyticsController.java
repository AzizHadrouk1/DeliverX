package com.esprit.microservice.driverclient.controller;

import com.esprit.microservice.driverclient.dto.DashboardStats;
import com.esprit.microservice.driverclient.service.AnalyticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/dashboard")
    public DashboardStats dashboard() {
        return analyticsService.getDashboardStats();
    }
}
