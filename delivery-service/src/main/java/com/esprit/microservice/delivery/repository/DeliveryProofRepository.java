package com.esprit.microservice.delivery.repository;

import com.esprit.microservice.delivery.entity.DeliveryProof;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeliveryProofRepository extends JpaRepository<DeliveryProof, Long> {

    Optional<DeliveryProof> findByDelivery_Id(Long deliveryId);

    boolean existsByDelivery_Id(Long deliveryId);
}
