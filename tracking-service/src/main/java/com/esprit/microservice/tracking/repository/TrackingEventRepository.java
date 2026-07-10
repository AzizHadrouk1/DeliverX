package com.esprit.microservice.tracking.repository;

import com.esprit.microservice.tracking.entity.TrackingEvent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface TrackingEventRepository extends MongoRepository<TrackingEvent, String> {

    /** All events for a delivery, newest first */
    List<TrackingEvent> findByDeliveryIdOrderByTimestampDesc(String deliveryId);

    /** Most recent GPS fix for a given delivery */
    Optional<TrackingEvent> findFirstByDeliveryIdOrderByTimestampDesc(String deliveryId);

    /** Events in a time window */
    List<TrackingEvent> findByDeliveryIdAndTimestampBetweenOrderByTimestampAsc(
            String deliveryId, Instant from, Instant to);

    /** Count events per delivery */
    long countByDeliveryId(String deliveryId);

    /** Delete all events for a delivery */
    void deleteByDeliveryId(String deliveryId);
}
