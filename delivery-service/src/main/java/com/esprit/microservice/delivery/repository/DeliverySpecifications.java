package com.esprit.microservice.delivery.repository;

import com.esprit.microservice.delivery.entity.Delivery;
import com.esprit.microservice.delivery.enums.DeliveryStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public final class DeliverySpecifications {

    private DeliverySpecifications() {
    }

    public static Specification<Delivery> withFilters(DeliveryStatus status, Long driverId, LocalDate date) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (driverId != null) {
                predicates.add(cb.equal(root.get("driverId"), driverId));
            }
            if (date != null) {
                LocalDateTime start = date.atStartOfDay();
                LocalDateTime end = date.atTime(LocalTime.MAX);
                predicates.add(cb.between(root.get("scheduledDate"), start, end));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
