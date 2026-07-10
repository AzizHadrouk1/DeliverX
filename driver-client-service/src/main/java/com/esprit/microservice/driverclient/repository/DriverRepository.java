package com.esprit.microservice.driverclient.repository;

import com.esprit.microservice.driverclient.model.Driver;
import com.esprit.microservice.driverclient.model.DriverStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface DriverRepository extends JpaRepository<Driver, Long> {

    List<Driver> findByStatus(DriverStatus status);

    long countByStatus(DriverStatus status);

    long countByCreatedAtAfter(LocalDateTime date);

    boolean existsByEmail(String email);

    boolean existsByLicenseNumber(String licenseNumber);

    @Query("""
            SELECT d FROM Driver d
            WHERE (:q IS NULL OR :q = '' OR
                   LOWER(d.firstName) LIKE LOWER(CONCAT('%', :q, '%')) OR
                   LOWER(d.lastName) LIKE LOWER(CONCAT('%', :q, '%')) OR
                   LOWER(d.email) LIKE LOWER(CONCAT('%', :q, '%')) OR
                   LOWER(d.licenseNumber) LIKE LOWER(CONCAT('%', :q, '%')))
            AND (:status IS NULL OR d.status = :status)
            """)
    Page<Driver> search(@Param("q") String q, @Param("status") DriverStatus status, Pageable pageable);
}
