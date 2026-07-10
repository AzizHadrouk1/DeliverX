package com.esprit.microservice.delivery.service.impl;

import com.esprit.microservice.delivery.dto.DeliveryProofDTO;
import com.esprit.microservice.delivery.dto.DeliveryProofResponseDTO;
import com.esprit.microservice.delivery.entity.Delivery;
import com.esprit.microservice.delivery.entity.DeliveryProof;
import com.esprit.microservice.delivery.exception.BadRequestException;
import com.esprit.microservice.delivery.exception.DeliveryNotFoundException;
import com.esprit.microservice.delivery.mapper.DeliveryProofMapper;
import com.esprit.microservice.delivery.repository.DeliveryProofRepository;
import com.esprit.microservice.delivery.repository.DeliveryRepository;
import com.esprit.microservice.delivery.service.DeliveryProofService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DeliveryProofServiceImpl implements DeliveryProofService {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryProofRepository deliveryProofRepository;
    private final DeliveryProofMapper deliveryProofMapper;

    public DeliveryProofServiceImpl(DeliveryRepository deliveryRepository,
                                    DeliveryProofRepository deliveryProofRepository,
                                    DeliveryProofMapper deliveryProofMapper) {
        this.deliveryRepository = deliveryRepository;
        this.deliveryProofRepository = deliveryProofRepository;
        this.deliveryProofMapper = deliveryProofMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public DeliveryProofResponseDTO getProof(Long deliveryId) {
        ensureDeliveryExists(deliveryId);
        return deliveryProofRepository.findByDelivery_Id(deliveryId)
                .map(deliveryProofMapper::toResponse)
                .orElseThrow(() -> new BadRequestException("No proof found for delivery: " + deliveryId));
    }

    @Override
    public DeliveryProofResponseDTO createProof(Long deliveryId, DeliveryProofDTO dto) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new DeliveryNotFoundException(deliveryId));

        if (deliveryProofRepository.existsByDelivery_Id(deliveryId)) {
            throw new BadRequestException("Proof already exists for delivery: " + deliveryId);
        }

        DeliveryProof proof = deliveryProofMapper.toEntity(dto, delivery);
        delivery.setProof(proof);
        deliveryProofRepository.save(proof);
        return deliveryProofMapper.toResponse(proof);
    }

    private void ensureDeliveryExists(Long deliveryId) {
        if (!deliveryRepository.existsById(deliveryId)) {
            throw new DeliveryNotFoundException(deliveryId);
        }
    }
}
