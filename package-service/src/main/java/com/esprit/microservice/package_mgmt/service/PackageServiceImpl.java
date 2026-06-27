package com.esprit.microservice.package_mgmt.service;

import com.esprit.microservice.package_mgmt.exception.PackageNotFoundException;
import com.esprit.microservice.package_mgmt.model.Package;
import com.esprit.microservice.package_mgmt.model.PackageDTO;
import com.esprit.microservice.package_mgmt.model.PackageStatus;
import com.esprit.microservice.package_mgmt.repository.PackageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PackageServiceImpl implements PackageService {

    private final PackageRepository packageRepository;

    @Override
    public List<PackageDTO> getAllPackages() {
        return packageRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PackageDTO getPackageById(Long id) {
        Package pkg = packageRepository.findById(id)
                .orElseThrow(() -> new PackageNotFoundException(id));
        return toDTO(pkg);
    }

    @Override
    public PackageDTO createPackage(PackageDTO packageDTO) {
        Package pkg = toEntity(packageDTO);
        Package saved = packageRepository.save(pkg);
        return toDTO(saved);
    }

    @Override
    public PackageDTO updatePackage(Long id, PackageDTO packageDTO) {
        Package existing = packageRepository.findById(id)
                .orElseThrow(() -> new PackageNotFoundException(id));

        existing.setTrackingNumber(packageDTO.getTrackingNumber());
        existing.setWeight(packageDTO.getWeight());
        existing.setDestination(packageDTO.getDestination());
        existing.setStatus(PackageStatus.valueOf(packageDTO.getStatus()));

        Package updated = packageRepository.save(existing);
        return toDTO(updated);
    }

    @Override
    public void deletePackage(Long id) {
        if (!packageRepository.existsById(id)) {
            throw new PackageNotFoundException(id);
        }
        packageRepository.deleteById(id);
    }

    private PackageDTO toDTO(Package pkg) {
        return PackageDTO.builder()
                .id(pkg.getId())
                .trackingNumber(pkg.getTrackingNumber())
                .weight(pkg.getWeight())
                .destination(pkg.getDestination())
                .status(pkg.getStatus().name())
                .build();
    }

    private Package toEntity(PackageDTO dto) {
        return Package.builder()
                .trackingNumber(dto.getTrackingNumber())
                .weight(dto.getWeight())
                .destination(dto.getDestination())
                .status(PackageStatus.valueOf(dto.getStatus()))
                .build();
    }
}
