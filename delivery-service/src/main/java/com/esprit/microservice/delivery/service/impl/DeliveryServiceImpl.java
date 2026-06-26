package com.esprit.microservice.delivery.service.impl;

import com.esprit.microservice.delivery.dto.DeliveryDTO;
import com.esprit.microservice.delivery.dto.DeliveryResponseDTO;
import com.esprit.microservice.delivery.dto.StatusUpdateDTO;
import com.esprit.microservice.delivery.entity.Delivery;
import com.esprit.microservice.delivery.enums.DeliveryStatus;
import com.esprit.microservice.delivery.exception.BadRequestException;
import com.esprit.microservice.delivery.exception.DeliveryAlreadyCompletedException;
import com.esprit.microservice.delivery.exception.DeliveryNotFoundException;
import com.esprit.microservice.delivery.mapper.DeliveryMapper;
import com.esprit.microservice.delivery.repository.DeliveryRepository;
import com.esprit.microservice.delivery.repository.DeliverySpecifications;
import com.esprit.microservice.delivery.service.DeliveryService;
import com.esprit.microservice.delivery.validation.StatusTransitionValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@Transactional
public class DeliveryServiceImpl implements DeliveryService {

    private static final Logger log = LoggerFactory.getLogger(DeliveryServiceImpl.class);

    private final DeliveryRepository deliveryRepository;
    private final DeliveryMapper deliveryMapper;
    private final StatusTransitionValidator statusTransitionValidator;

    public DeliveryServiceImpl(DeliveryRepository deliveryRepository,
                               DeliveryMapper deliveryMapper,
                               StatusTransitionValidator statusTransitionValidator) {
        this.deliveryRepository = deliveryRepository;
        this.deliveryMapper = deliveryMapper;
        this.statusTransitionValidator = statusTransitionValidator;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DeliveryResponseDTO> findAll(Pageable pageable, DeliveryStatus status, Long driverId, LocalDate date) {
        return deliveryRepository.findAll(DeliverySpecifications.withFilters(status, driverId, date), pageable)
                .map(deliveryMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public DeliveryResponseDTO findById(Long id) {
        return deliveryMapper.toResponse(getDeliveryOrThrow(id));
    }

    @Override
    public DeliveryResponseDTO create(DeliveryDTO dto) {
        Delivery delivery = deliveryMapper.toEntity(dto);
        return deliveryMapper.toResponse(deliveryRepository.save(delivery));
    }

    @Override
    public DeliveryResponseDTO update(Long id, DeliveryDTO dto) {
        Delivery delivery = getDeliveryOrThrow(id);
        if (delivery.getStatus() != DeliveryStatus.PENDING) {
            throw new BadRequestException("Delivery can only be updated when status is PENDING");
        }
        deliveryMapper.updateEntity(delivery, dto);
        return deliveryMapper.toResponse(deliveryRepository.save(delivery));
    }

    @Override
    public DeliveryResponseDTO updateStatus(Long id, StatusUpdateDTO statusUpdate) {
        Delivery delivery = getDeliveryOrThrow(id);
        DeliveryStatus current = delivery.getStatus();
        DeliveryStatus target = statusUpdate.getStatus();

        statusTransitionValidator.validate(current, target);

        if (statusUpdate.getNote() != null && !statusUpdate.getNote().isBlank()) {
            log.info("Status update for delivery {}: {} -> {} | note: {}", id, current, target, statusUpdate.getNote());
        }

        delivery.setStatus(target);
        if (target == DeliveryStatus.DELIVERED) {
            delivery.setActualDeliveryDate(LocalDateTime.now());
        }

        return deliveryMapper.toResponse(deliveryRepository.save(delivery));
    }

    @Override
    public void deleteOrCancel(Long id) {
        Delivery delivery = getDeliveryOrThrow(id);
        DeliveryStatus status = delivery.getStatus();

        if (statusTransitionValidator.isTerminal(status)) {
            throw new DeliveryAlreadyCompletedException(id);
        }

        if (status == DeliveryStatus.PENDING) {
            deliveryRepository.delete(delivery);
            return;
        }

        statusTransitionValidator.validate(status, DeliveryStatus.CANCELLED);
        delivery.setStatus(DeliveryStatus.CANCELLED);
        deliveryRepository.save(delivery);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryResponseDTO> findByDriverId(Long driverId) {
        return deliveryRepository.findByDriverId(driverId).stream()
                .map(deliveryMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryResponseDTO> findByScheduledDate(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);
        return deliveryRepository.findByScheduledDateBetween(start, end).stream()
                .map(deliveryMapper::toResponse)
                .toList();
    }

    private Delivery getDeliveryOrThrow(Long id) {
        return deliveryRepository.findById(id)
                .orElseThrow(() -> new DeliveryNotFoundException(id));
    }
}
