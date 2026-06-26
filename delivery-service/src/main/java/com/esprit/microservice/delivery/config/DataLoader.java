package com.esprit.microservice.delivery.config;

import com.esprit.microservice.delivery.entity.Delivery;
import com.esprit.microservice.delivery.enums.DeliveryStatus;
import com.esprit.microservice.delivery.repository.DeliveryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner seedDeliveries(DeliveryRepository deliveryRepository) {
        return args -> {
            if (deliveryRepository.count() > 0) {
                return;
            }

            Delivery delivery1 = buildDelivery(1L, 1L, 1L, 1L,
                    "12 Rue de la Paix, Paris", "45 Avenue Victor Hugo, Lyon",
                    LocalDateTime.now().plusDays(1), DeliveryStatus.PENDING);
            Delivery delivery2 = buildDelivery(2L, 2L, 2L, 2L,
                    "8 Boulevard Haussmann, Paris", "3 Place Bellecour, Lyon",
                    LocalDateTime.now().plusDays(2), DeliveryStatus.ASSIGNED);
            Delivery delivery3 = buildDelivery(3L, 3L, 1L, 1L,
                    "22 Rue Nationale, Lille", "10 Quai des Chartrons, Bordeaux",
                    LocalDateTime.now().plusDays(3), DeliveryStatus.IN_PROGRESS);

            deliveryRepository.save(delivery1);
            deliveryRepository.save(delivery2);
            deliveryRepository.save(delivery3);
        };
    }

    private Delivery buildDelivery(Long packageId, Long clientId, Long driverId, Long vehicleId,
                                   String pickup, String deliveryAddress,
                                   LocalDateTime scheduledDate, DeliveryStatus status) {
        Delivery delivery = new Delivery();
        delivery.setPackageId(packageId);
        delivery.setClientId(clientId);
        delivery.setDriverId(driverId);
        delivery.setVehicleId(vehicleId);
        delivery.setPickupAddress(pickup);
        delivery.setDeliveryAddress(deliveryAddress);
        delivery.setScheduledDate(scheduledDate);
        delivery.setStatus(status);
        return delivery;
    }
}
