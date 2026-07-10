package com.esprit.microservice.package_mgmt.mapper;

import com.esprit.microservice.package_mgmt.dto.PackageDTO;
import com.esprit.microservice.package_mgmt.dto.PackageResponseDTO;
import com.esprit.microservice.package_mgmt.dto.PackageStatusHistoryDTO;
import com.esprit.microservice.package_mgmt.entity.PackageEntity;
import com.esprit.microservice.package_mgmt.entity.PackageStatusHistory;
import com.esprit.microservice.package_mgmt.enums.PackageStatus;
import org.springframework.stereotype.Component;

@Component
public class PackageMapper {

    public PackageEntity toEntity(PackageDTO dto) {
        PackageEntity entity = new PackageEntity();
        entity.setWeight(dto.getWeight());
        entity.setWidth(dto.getWidth());
        entity.setHeight(dto.getHeight());
        entity.setDepth(dto.getDepth());
        entity.setDescription(dto.getDescription());
        entity.setClientId(dto.getClientId());
        entity.setStatus(PackageStatus.CREATED);
        return entity;
    }

    public void updateEntity(PackageEntity entity, PackageDTO dto) {
        entity.setWeight(dto.getWeight());
        entity.setWidth(dto.getWidth());
        entity.setHeight(dto.getHeight());
        entity.setDepth(dto.getDepth());
        entity.setDescription(dto.getDescription());
        entity.setClientId(dto.getClientId());
    }

    public PackageResponseDTO toResponse(PackageEntity entity) {
        PackageResponseDTO dto = new PackageResponseDTO();
        dto.setId(entity.getId());
        dto.setTrackingNumber(entity.getTrackingNumber());
        dto.setWeight(entity.getWeight());
        dto.setWidth(entity.getWidth());
        dto.setHeight(entity.getHeight());
        dto.setDepth(entity.getDepth());
        dto.setDescription(entity.getDescription());
        dto.setStatus(entity.getStatus());
        dto.setClientId(entity.getClientId());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setDestination(null);
        return dto;
    }

    public PackageStatusHistoryDTO toHistoryDto(PackageStatusHistory history) {
        PackageStatusHistoryDTO dto = new PackageStatusHistoryDTO();
        dto.setId(history.getId());
        dto.setPackageId(history.getPackageId());
        dto.setStatus(history.getStatus());
        dto.setTimestamp(history.getTimestamp());
        dto.setComment(history.getComment());
        return dto;
    }
}
