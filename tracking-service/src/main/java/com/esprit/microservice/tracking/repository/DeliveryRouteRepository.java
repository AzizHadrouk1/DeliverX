package com.esprit.microservice.tracking.repository;

import com.esprit.microservice.tracking.entity.DeliveryRoute;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeliveryRouteRepository extends MongoRepository<DeliveryRoute, String> {

    Optional<DeliveryRoute> findByDeliveryId(String deliveryId);

    void deleteByDeliveryId(String deliveryId);
}
