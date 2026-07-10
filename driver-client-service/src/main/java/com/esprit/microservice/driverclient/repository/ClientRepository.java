package com.esprit.microservice.driverclient.repository;

import com.esprit.microservice.driverclient.model.Client;
import com.esprit.microservice.driverclient.model.ClientStatus;
import com.esprit.microservice.driverclient.model.ClientType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Long> {

    List<Client> findByStatus(ClientStatus status);

    List<Client> findByType(ClientType type);

    long countByStatus(ClientStatus status);

    long countByType(ClientType type);

    long countByCreatedAtAfter(LocalDateTime date);

    boolean existsByEmail(String email);

    java.util.Optional<Client> findByEmail(String email);

    @Query("""
            SELECT c FROM Client c
            WHERE (:q IS NULL OR :q = '' OR
                   LOWER(c.firstName) LIKE LOWER(CONCAT('%', :q, '%')) OR
                   LOWER(c.lastName) LIKE LOWER(CONCAT('%', :q, '%')) OR
                   LOWER(c.email) LIKE LOWER(CONCAT('%', :q, '%')) OR
                   LOWER(c.city) LIKE LOWER(CONCAT('%', :q, '%')) OR
                   LOWER(COALESCE(c.companyName, '')) LIKE LOWER(CONCAT('%', :q, '%')))
            AND (:status IS NULL OR c.status = :status)
            AND (:type IS NULL OR c.type = :type)
            """)
    Page<Client> search(@Param("q") String q,
                        @Param("status") ClientStatus status,
                        @Param("type") ClientType type,
                        Pageable pageable);
}
