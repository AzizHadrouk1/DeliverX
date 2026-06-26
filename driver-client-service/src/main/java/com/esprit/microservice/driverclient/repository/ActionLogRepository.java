package com.esprit.microservice.driverclient.repository;

import com.esprit.microservice.driverclient.model.ActionLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActionLogRepository extends JpaRepository<ActionLog, Long> {

    Page<ActionLog> findByEntityTypeOrderByCreatedAtDesc(String entityType, Pageable pageable);
}
