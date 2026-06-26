package com.esprit.microservice.delivery.service;

import com.esprit.microservice.delivery.dto.DeliveryDTO;
import com.esprit.microservice.delivery.dto.DeliveryResponseDTO;
import com.esprit.microservice.delivery.dto.StatusUpdateDTO;
import com.esprit.microservice.delivery.enums.DeliveryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface DeliveryService {

    Page<DeliveryResponseDTO> findAll(Pageable pageable, DeliveryStatus status, Long driverId, LocalDate date);

    DeliveryResponseDTO findById(Long id);

    DeliveryResponseDTO create(DeliveryDTO dto);

    DeliveryResponseDTO update(Long id, DeliveryDTO dto);

    DeliveryResponseDTO updateStatus(Long id, StatusUpdateDTO statusUpdate);

    void deleteOrCancel(Long id);

    List<DeliveryResponseDTO> findByDriverId(Long driverId);

    List<DeliveryResponseDTO> findByScheduledDate(LocalDate date);
}
