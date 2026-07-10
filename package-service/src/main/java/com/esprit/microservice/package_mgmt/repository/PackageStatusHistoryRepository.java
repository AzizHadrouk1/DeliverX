package com.esprit.microservice.package_mgmt.repository;

import com.esprit.microservice.package_mgmt.entity.PackageStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PackageStatusHistoryRepository extends JpaRepository<PackageStatusHistory, Long> {

    List<PackageStatusHistory> findByPackageIdOrderByTimestampAsc(Long packageId);

    void deleteByPackageId(Long packageId);
}
