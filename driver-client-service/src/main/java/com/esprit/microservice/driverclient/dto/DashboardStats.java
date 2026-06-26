package com.esprit.microservice.driverclient.dto;

import java.util.Map;

public record DashboardStats(
        long totalDrivers,
        long activeDrivers,
        long totalClients,
        long activeClients,
        Map<String, Long> driversByStatus,
        Map<String, Long> clientsByType,
        long recentDriverRegistrations,
        long recentClientRegistrations
) {}
