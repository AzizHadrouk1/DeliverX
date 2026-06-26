package com.esprit.microservice.driverclient.service;

import com.esprit.microservice.driverclient.dto.DashboardStats;
import com.esprit.microservice.driverclient.model.ClientStatus;
import com.esprit.microservice.driverclient.model.ClientType;
import com.esprit.microservice.driverclient.model.DriverStatus;
import com.esprit.microservice.driverclient.repository.ClientRepository;
import com.esprit.microservice.driverclient.repository.DriverRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AnalyticsService {

    private final DriverRepository driverRepository;
    private final ClientRepository clientRepository;

    public AnalyticsService(DriverRepository driverRepository, ClientRepository clientRepository) {
        this.driverRepository = driverRepository;
        this.clientRepository = clientRepository;
    }

    public DashboardStats getDashboardStats() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        Map<String, Long> driversByStatus = Arrays.stream(DriverStatus.values())
                .collect(Collectors.toMap(Enum::name, driverRepository::countByStatus));

        Map<String, Long> clientsByType = Arrays.stream(ClientType.values())
                .collect(Collectors.toMap(Enum::name, clientRepository::countByType));

        return new DashboardStats(
                driverRepository.count(),
                driverRepository.countByStatus(DriverStatus.AVAILABLE)
                        + driverRepository.countByStatus(DriverStatus.ON_DELIVERY),
                clientRepository.count(),
                clientRepository.countByStatus(ClientStatus.ACTIVE),
                driversByStatus,
                clientsByType,
                driverRepository.countByCreatedAtAfter(thirtyDaysAgo),
                clientRepository.countByCreatedAtAfter(thirtyDaysAgo)
        );
    }
}
