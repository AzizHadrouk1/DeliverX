package com.esprit.microservice.assignment.service;


import com.esprit.microservice.assignment.client.DeliveryClient;
import com.esprit.microservice.assignment.client.DriverClient;
import com.esprit.microservice.assignment.client.VehicleClient;
import com.esprit.microservice.assignment.dto.AssignmentDTO;
import com.esprit.microservice.assignment.dto.AssignmentResponseDTO;
import com.esprit.microservice.assignment.dto.DeliveryDTO;
import com.esprit.microservice.assignment.dto.DriverDTO;
import com.esprit.microservice.assignment.dto.StatusUpdateDTO;
import com.esprit.microservice.assignment.dto.VehicleDTO;
import com.esprit.microservice.assignment.exception.AssignmentAlreadyFinalizedException;
import com.esprit.microservice.assignment.exception.AssignmentNotFoundException;
import com.esprit.microservice.assignment.exception.BadRequestException;
import com.esprit.microservice.assignment.exception.ExternalServiceException;
import com.esprit.microservice.assignment.mapper.AssignmentMapper;
import com.esprit.microservice.assignment.model.Assignment;
import com.esprit.microservice.assignment.model.AssignmentStatus;
import com.esprit.microservice.assignment.repository.AssignmentRepository;
import com.esprit.microservice.assignment.validation.StatusTransitionValidator;
import feign.FeignException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class AssignmentService {

    private static final String AVAILABLE = "AVAILABLE";
    private static final String PENDING = "PENDING";
    private static final List<AssignmentStatus> ACTIVE_STATUSES =
            List.of(AssignmentStatus.ASSIGNED, AssignmentStatus.IN_PROGRESS);

    private final AssignmentRepository repository;
    private final AssignmentMapper mapper;
    private final StatusTransitionValidator transitionValidator;
    private final DriverClient driverClient;
    private final VehicleClient vehicleClient;
    private final DeliveryClient deliveryClient;

    public AssignmentService(AssignmentRepository repository,
                              AssignmentMapper mapper,
                              StatusTransitionValidator transitionValidator,
                              DriverClient driverClient,
                              VehicleClient vehicleClient,
                              DeliveryClient deliveryClient) {
        this.repository = repository;
        this.mapper = mapper;
        this.transitionValidator = transitionValidator;
        this.driverClient = driverClient;
        this.vehicleClient = vehicleClient;
        this.deliveryClient = deliveryClient;
    }

    @Transactional(readOnly = true)
    public List<AssignmentResponseDTO> getAllAssignments() {
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public AssignmentResponseDTO getAssignmentById(Long id) {
        return mapper.toResponse(getOrThrow(id));
    }

    public AssignmentResponseDTO createAssignment(AssignmentDTO dto) {
        DeliveryDTO delivery = fetchDelivery(dto.getDeliveryId());
        if (!PENDING.equalsIgnoreCase(delivery.status())) {
            throw new BadRequestException("Delivery is not pending: " + dto.getDeliveryId());
        }
        if (repository.existsByDeliveryIdAndStatusNot(dto.getDeliveryId(), AssignmentStatus.CANCELLED)) {
            throw new BadRequestException("Delivery already has an active assignment: " + dto.getDeliveryId());
        }

        DriverDTO driver = fetchDriver(dto.getDriverId());
        if (!AVAILABLE.equalsIgnoreCase(driver.status())) {
            throw new BadRequestException("Driver is not available: " + dto.getDriverId());
        }
        if (repository.existsByDriverIdAndStatusIn(dto.getDriverId(), ACTIVE_STATUSES)) {
            throw new BadRequestException("Driver already has an active assignment: " + dto.getDriverId());
        }

        VehicleDTO vehicle = fetchVehicle(dto.getVehicleId());
        if (!AVAILABLE.equalsIgnoreCase(vehicle.status())) {
            throw new BadRequestException("Vehicle is not available: " + dto.getVehicleId());
        }
        if (repository.existsByVehicleIdAndStatusIn(dto.getVehicleId(), ACTIVE_STATUSES)) {
            throw new BadRequestException("Vehicle already has an active assignment: " + dto.getVehicleId());
        }

        Assignment assignment = mapper.toEntity(dto);
        assignment.setStatus(AssignmentStatus.ASSIGNED);
        assignment.setAssignedAt(LocalDateTime.now());
        assignment.setUpdatedAt(LocalDateTime.now());

        return mapper.toResponse(repository.save(assignment));
    }

    public AssignmentResponseDTO updateAssignment(Long id, AssignmentDTO dto) {
        Assignment assignment = getOrThrow(id);
        if (assignment.getStatus() != AssignmentStatus.ASSIGNED) {
            throw new BadRequestException("Assignment can only be edited while ASSIGNED: " + id);
        }

        if (!dto.getDriverId().equals(assignment.getDriverId())) {
            DriverDTO driver = fetchDriver(dto.getDriverId());
            if (!AVAILABLE.equalsIgnoreCase(driver.status())) {
                throw new BadRequestException("Driver is not available: " + dto.getDriverId());
            }
            if (repository.existsByDriverIdAndStatusIn(dto.getDriverId(), ACTIVE_STATUSES)) {
                throw new BadRequestException("Driver already has an active assignment: " + dto.getDriverId());
            }
        }

        if (!dto.getVehicleId().equals(assignment.getVehicleId())) {
            VehicleDTO vehicle = fetchVehicle(dto.getVehicleId());
            if (!AVAILABLE.equalsIgnoreCase(vehicle.status())) {
                throw new BadRequestException("Vehicle is not available: " + dto.getVehicleId());
            }
            if (repository.existsByVehicleIdAndStatusIn(dto.getVehicleId(), ACTIVE_STATUSES)) {
                throw new BadRequestException("Vehicle already has an active assignment: " + dto.getVehicleId());
            }
        }

        if (!dto.getDeliveryId().equals(assignment.getDeliveryId())) {
            fetchDelivery(dto.getDeliveryId());
        }

        mapper.updateEntity(assignment, dto);
        assignment.setUpdatedAt(LocalDateTime.now());
        return mapper.toResponse(repository.save(assignment));
    }

    public AssignmentResponseDTO updateStatus(Long id, StatusUpdateDTO statusUpdate) {
        Assignment assignment = getOrThrow(id);
        transitionValidator.validate(assignment.getStatus(), statusUpdate.getStatus());
        assignment.setStatus(statusUpdate.getStatus());
        assignment.setUpdatedAt(LocalDateTime.now());
        return mapper.toResponse(repository.save(assignment));
    }

    public void deleteAssignment(Long id) {
        Assignment assignment = getOrThrow(id);
        if (transitionValidator.isTerminal(assignment.getStatus())) {
            throw new AssignmentAlreadyFinalizedException(id);
        }
        repository.delete(assignment);
    }

    @Transactional(readOnly = true)
    public List<AssignmentResponseDTO> getAssignmentsByDriver(Long driverId) {
        return repository.findByDriverId(driverId).stream().map(mapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<AssignmentResponseDTO> getAssignmentsByDelivery(Long deliveryId) {
        return repository.findByDeliveryId(deliveryId).stream().map(mapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<AssignmentResponseDTO> getAssignmentsByStatus(AssignmentStatus status) {
        return repository.findByStatus(status).stream().map(mapper::toResponse).toList();
    }

    private Assignment getOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new AssignmentNotFoundException(id));
    }

    private DriverDTO fetchDriver(Long id) {
        try {
            return driverClient.getDriver(id);
        } catch (FeignException.NotFound e) {
            throw new BadRequestException("Driver not found: " + id);
        } catch (FeignException e) {
            throw new ExternalServiceException("Driver service unavailable");
        }
    }

    private VehicleDTO fetchVehicle(Long id) {
        try {
            return vehicleClient.getVehicle(id);
        } catch (FeignException.NotFound e) {
            throw new BadRequestException("Vehicle not found: " + id);
        } catch (FeignException e) {
            throw new ExternalServiceException("Vehicle service unavailable");
        }
    }

    private DeliveryDTO fetchDelivery(Long id) {
        try {
            return deliveryClient.getDelivery(id);
        } catch (FeignException.NotFound e) {
            throw new BadRequestException("Delivery not found: " + id);
        } catch (FeignException e) {
            throw new ExternalServiceException("Delivery service unavailable");
        }
    }
}
