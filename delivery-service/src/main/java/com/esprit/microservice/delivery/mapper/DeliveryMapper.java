package com.esprit.microservice.delivery.mapper;

import com.esprit.microservice.delivery.dto.DeliveryDTO;
import com.esprit.microservice.delivery.dto.DeliveryResponseDTO;
import com.esprit.microservice.delivery.entity.Delivery;
import com.esprit.microservice.delivery.enums.DeliveryStatus;
import org.springframework.stereotype.Component;

@Component
public class DeliveryMapper {

    private final DeliveryProofMapper deliveryProofMapper;

    public DeliveryMapper(DeliveryProofMapper deliveryProofMapper) {
        this.deliveryProofMapper = deliveryProofMapper;
    }

    public Delivery toEntity(DeliveryDTO dto) {
        Delivery delivery = new Delivery();
        delivery.setPackageId(dto.getPackageId());
        delivery.setClientId(dto.getClientId());
        delivery.setDriverId(dto.getDriverId());
        delivery.setVehicleId(dto.getVehicleId());
        delivery.setPickupAddress(dto.getPickupAddress());
        delivery.setDeliveryAddress(dto.getDeliveryAddress());
        delivery.setScheduledDate(dto.getScheduledDate());
        delivery.setStatus(DeliveryStatus.PENDING);
        return delivery;
    }

    public void updateEntity(Delivery delivery, DeliveryDTO dto) {
        delivery.setPackageId(dto.getPackageId());
        delivery.setClientId(dto.getClientId());
        delivery.setDriverId(dto.getDriverId());
        delivery.setVehicleId(dto.getVehicleId());
        delivery.setPickupAddress(dto.getPickupAddress());
        delivery.setDeliveryAddress(dto.getDeliveryAddress());
        delivery.setScheduledDate(dto.getScheduledDate());
    }

    public DeliveryResponseDTO toResponse(Delivery delivery) {
        DeliveryResponseDTO dto = new DeliveryResponseDTO();
        dto.setId(delivery.getId());
        dto.setPackageId(delivery.getPackageId());
        dto.setClientId(delivery.getClientId());
        dto.setDriverId(delivery.getDriverId());
        dto.setVehicleId(delivery.getVehicleId());
        dto.setPickupAddress(delivery.getPickupAddress());
        dto.setDeliveryAddress(delivery.getDeliveryAddress());
        dto.setScheduledDate(delivery.getScheduledDate());
        dto.setActualDeliveryDate(delivery.getActualDeliveryDate());
        dto.setStatus(delivery.getStatus());
        dto.setCreatedAt(delivery.getCreatedAt());
        if (delivery.getProof() != null) {
            dto.setProof(deliveryProofMapper.toResponse(delivery.getProof()));
        }
        return dto;
    }
}
