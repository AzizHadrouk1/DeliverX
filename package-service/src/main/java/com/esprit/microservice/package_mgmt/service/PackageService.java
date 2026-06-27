package com.esprit.microservice.package_mgmt.service;

import com.esprit.microservice.package_mgmt.model.PackageDTO;

import java.util.List;

public interface PackageService {

    List<PackageDTO> getAllPackages();

    PackageDTO getPackageById(Long id);

    PackageDTO createPackage(PackageDTO packageDTO);

    PackageDTO updatePackage(Long id, PackageDTO packageDTO);

    void deletePackage(Long id);
}
