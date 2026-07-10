package com.esprit.microservice.assignment.repository;


import com.esprit.microservice.assignment.model.Assignment;
import com.esprit.microservice.assignment.model.AssignmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    List<Assignment> findByDriverId(Long driverId);

    List<Assignment> findByDeliveryId(Long deliveryId);

    List<Assignment> findByStatus(AssignmentStatus status);

    boolean existsByDeliveryIdAndStatusNot(Long deliveryId, AssignmentStatus status);

    boolean existsByDriverIdAndStatusIn(Long driverId, List<AssignmentStatus> statuses);

    boolean existsByVehicleIdAndStatusIn(Long vehicleId, List<AssignmentStatus> statuses);
}
