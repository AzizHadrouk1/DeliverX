package com.esprit.microservice.assignment.repository;


import com.esprit.microservice.assignment.model.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    List<Assignment> findByDriverId(Long driverId);

    List<Assignment> findByDeliveryId(Long deliveryId);

    List<Assignment> findByStatus(String status);
}
