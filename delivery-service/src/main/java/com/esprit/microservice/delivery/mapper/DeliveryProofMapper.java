package com.esprit.microservice.delivery.mapper;

import com.esprit.microservice.delivery.dto.DeliveryProofDTO;
import com.esprit.microservice.delivery.dto.DeliveryProofResponseDTO;
import com.esprit.microservice.delivery.entity.Delivery;
import com.esprit.microservice.delivery.entity.DeliveryProof;
import org.springframework.stereotype.Component;

@Component
public class DeliveryProofMapper {

    public DeliveryProof toEntity(DeliveryProofDTO dto, Delivery delivery) {
        DeliveryProof proof = new DeliveryProof();
        proof.setDelivery(delivery);
        proof.setPhotoUrl(dto.getPhotoUrl());
        proof.setSignature(dto.getSignature());
        proof.setRecipientName(dto.getRecipientName());
        return proof;
    }

    public DeliveryProofResponseDTO toResponse(DeliveryProof proof) {
        DeliveryProofResponseDTO dto = new DeliveryProofResponseDTO();
        dto.setId(proof.getId());
        dto.setDeliveryId(proof.getDelivery().getId());
        dto.setPhotoUrl(proof.getPhotoUrl());
        dto.setSignature(proof.getSignature());
        dto.setRecipientName(proof.getRecipientName());
        dto.setTimestamp(proof.getTimestamp());
        return dto;
    }
}
