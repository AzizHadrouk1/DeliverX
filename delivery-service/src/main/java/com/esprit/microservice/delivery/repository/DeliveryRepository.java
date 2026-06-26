package com.esprit.microservice.delivery.repository;

import com.esprit.microservice.delivery.entity.Delivery;
import com.esprit.microservice.delivery.enums.DeliveryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;

public interface DeliveryRepository extends JpaRepository<Delivery, Long>, JpaSpecificationExecutor<Delivery> {

    Page<Delivery> findByStatus(DeliveryStatus status, Pageable pageable);

    Page<Delivery> findByDriverId(Long driverId, Pageable pageable);

    List<Delivery> findByDriverId(Long driverId);

    List<Delivery> findByScheduledDateBetween(LocalDateTime start, LocalDateTime end);
}
