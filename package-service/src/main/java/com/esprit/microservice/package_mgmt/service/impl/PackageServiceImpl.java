package com.esprit.microservice.package_mgmt.service.impl;

import com.esprit.microservice.package_mgmt.dto.PackageDTO;
import com.esprit.microservice.package_mgmt.dto.PackageResponseDTO;
import com.esprit.microservice.package_mgmt.dto.PackageStatusHistoryDTO;
import com.esprit.microservice.package_mgmt.dto.UpdatePackageStatusDTO;
import com.esprit.microservice.package_mgmt.entity.PackageEntity;
import com.esprit.microservice.package_mgmt.entity.PackageStatusHistory;
import com.esprit.microservice.package_mgmt.enums.PackageStatus;
import com.esprit.microservice.package_mgmt.exception.PackageDeleteConflictException;
import com.esprit.microservice.package_mgmt.exception.PackageNotFoundException;
import com.esprit.microservice.package_mgmt.mapper.PackageMapper;
import com.esprit.microservice.package_mgmt.repository.PackageRepository;
import com.esprit.microservice.package_mgmt.repository.PackageSpecifications;
import com.esprit.microservice.package_mgmt.repository.PackageStatusHistoryRepository;
import com.esprit.microservice.package_mgmt.service.PackageService;
import com.esprit.microservice.package_mgmt.validation.StatusTransitionValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PackageServiceImpl implements PackageService {

    private final PackageRepository packageRepository;
    private final PackageStatusHistoryRepository historyRepository;
    private final PackageMapper packageMapper;
    private final StatusTransitionValidator statusTransitionValidator;

    public PackageServiceImpl(PackageRepository packageRepository,
                              PackageStatusHistoryRepository historyRepository,
                              PackageMapper packageMapper,
                              StatusTransitionValidator statusTransitionValidator) {
        this.packageRepository = packageRepository;
        this.historyRepository = historyRepository;
        this.packageMapper = packageMapper;
        this.statusTransitionValidator = statusTransitionValidator;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PackageResponseDTO> findAll(Pageable pageable, PackageStatus status, Long clientId) {
        return packageRepository.findAll(PackageSpecifications.withFilters(status, clientId), pageable)
                .map(packageMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public PackageResponseDTO findById(Long id) {
        return packageMapper.toResponse(getPackageOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PackageResponseDTO findByTrackingNumber(String trackingNumber) {
        PackageEntity entity = packageRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new PackageNotFoundException(trackingNumber));
        return packageMapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PackageResponseDTO> findByClientId(Long clientId) {
        return packageRepository.findByClientId(clientId).stream()
                .map(packageMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PackageStatusHistoryDTO> getHistory(Long id) {
        getPackageOrThrow(id);
        return historyRepository.findByPackageIdOrderByTimestampAsc(id).stream()
                .map(packageMapper::toHistoryDto)
                .toList();
    }

    @Override
    public PackageResponseDTO create(PackageDTO dto) {
        PackageEntity entity = packageMapper.toEntity(dto);
        entity.setTrackingNumber(generateUniqueTrackingNumber());
        PackageEntity saved = packageRepository.save(entity);
        appendHistory(saved.getId(), PackageStatus.CREATED, "Package created");
        return packageMapper.toResponse(saved);
    }

    @Override
    public PackageResponseDTO update(Long id, PackageDTO dto) {
        PackageEntity entity = getPackageOrThrow(id);
        packageMapper.updateEntity(entity, dto);
        return packageMapper.toResponse(packageRepository.save(entity));
    }

    @Override
    public PackageResponseDTO updateStatus(Long id, UpdatePackageStatusDTO statusUpdate) {
        PackageEntity entity = getPackageOrThrow(id);
        PackageStatus current = entity.getStatus();
        PackageStatus target = statusUpdate.getStatus();

        statusTransitionValidator.validate(current, target);

        entity.setStatus(target);
        PackageEntity saved = packageRepository.save(entity);
        appendHistory(saved.getId(), target, statusUpdate.getComment());
        return packageMapper.toResponse(saved);
    }

    @Override
    public void delete(Long id) {
        PackageEntity entity = getPackageOrThrow(id);
        if (entity.getStatus() != PackageStatus.CREATED) {
            throw new PackageDeleteConflictException(id, entity.getStatus().name());
        }
        historyRepository.deleteByPackageId(id);
        packageRepository.delete(entity);
    }

    private PackageEntity getPackageOrThrow(Long id) {
        return packageRepository.findById(id)
                .orElseThrow(() -> new PackageNotFoundException(id));
    }

    private void appendHistory(Long packageId, PackageStatus status, String comment) {
        PackageStatusHistory history = new PackageStatusHistory();
        history.setPackageId(packageId);
        history.setStatus(status);
        history.setComment(comment);
        historyRepository.save(history);
    }

    private String generateUniqueTrackingNumber() {
        String trackingNumber;
        do {
            trackingNumber = "DX-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        } while (packageRepository.existsByTrackingNumber(trackingNumber));
        return trackingNumber;
    }
}
