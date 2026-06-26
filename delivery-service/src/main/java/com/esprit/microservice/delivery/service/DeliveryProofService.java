package com.esprit.microservice.delivery.service;

import com.esprit.microservice.delivery.dto.DeliveryProofDTO;
import com.esprit.microservice.delivery.dto.DeliveryProofResponseDTO;

public interface DeliveryProofService {

    DeliveryProofResponseDTO getProof(Long deliveryId);

    DeliveryProofResponseDTO createProof(Long deliveryId, DeliveryProofDTO dto);
}
