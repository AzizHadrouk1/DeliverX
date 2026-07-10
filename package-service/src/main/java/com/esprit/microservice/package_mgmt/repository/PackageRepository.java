package com.esprit.microservice.package_mgmt.repository;

<<<<<<< Updated upstream
import com.esprit.microservice.package_mgmt.model.Package;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PackageRepository extends JpaRepository<Package, Long> {

    Optional<Package> findByTrackingNumber(String trackingNumber);

    boolean existsByTrackingNumber(String trackingNumber);
=======
import com.esprit.microservice.package_mgmt.entity.PackageEntity;
import com.esprit.microservice.package_mgmt.enums.PackageStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface PackageRepository extends JpaRepository<PackageEntity, Long>, JpaSpecificationExecutor<PackageEntity> {

    Optional<PackageEntity> findByTrackingNumber(String trackingNumber);

    List<PackageEntity> findByClientId(Long clientId);

    boolean existsByTrackingNumber(String trackingNumber);

    List<PackageEntity> findByStatus(PackageStatus status);
>>>>>>> Stashed changes
}
