package com.esprit.microservice.package_mgmt.service;

<<<<<<< Updated upstream
import com.esprit.microservice.package_mgmt.model.PackageDTO;
=======
import com.esprit.microservice.package_mgmt.dto.PackageDTO;
import com.esprit.microservice.package_mgmt.dto.PackageResponseDTO;
import com.esprit.microservice.package_mgmt.dto.PackageStatusHistoryDTO;
import com.esprit.microservice.package_mgmt.dto.UpdatePackageStatusDTO;
import com.esprit.microservice.package_mgmt.enums.PackageStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
>>>>>>> Stashed changes

import java.util.List;

public interface PackageService {

<<<<<<< Updated upstream
    List<PackageDTO> getAllPackages();

    PackageDTO getPackageById(Long id);

    PackageDTO createPackage(PackageDTO packageDTO);

    PackageDTO updatePackage(Long id, PackageDTO packageDTO);

    void deletePackage(Long id);
=======
    Page<PackageResponseDTO> findAll(Pageable pageable, PackageStatus status, Long clientId);

    PackageResponseDTO findById(Long id);

    PackageResponseDTO findByTrackingNumber(String trackingNumber);

    List<PackageResponseDTO> findByClientId(Long clientId);

    List<PackageStatusHistoryDTO> getHistory(Long id);

    PackageResponseDTO create(PackageDTO dto);

    PackageResponseDTO update(Long id, PackageDTO dto);

    PackageResponseDTO updateStatus(Long id, UpdatePackageStatusDTO statusUpdate);

    void delete(Long id);
>>>>>>> Stashed changes
}
