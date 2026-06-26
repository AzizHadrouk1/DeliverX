package com.esprit.microservice.driverclient.repository;

import com.esprit.microservice.driverclient.model.StoredFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoredFileRepository extends JpaRepository<StoredFile, Long> {

    List<StoredFile> findByOwnerTypeAndOwnerId(String ownerType, Long ownerId);
}
