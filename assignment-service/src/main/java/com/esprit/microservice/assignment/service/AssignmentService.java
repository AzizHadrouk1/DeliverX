package com.esprit.microservice.assignment.service;

import com.esprit.microservice.assignment.dto.AssignmentCreatedEvent;
import com.esprit.microservice.assignment.dto.AssignmentDTO;
import com.esprit.microservice.assignment.dto.AssignmentDetailsDTO;
import com.esprit.microservice.assignment.dto.AssignmentResponseDTO;
import com.esprit.microservice.assignment.dto.AssignmentStatusChangedEvent;
import com.esprit.microservice.assignment.dto.DeliveryDTO;
import com.esprit.microservice.assignment.dto.DriverDTO;
import com.esprit.microservice.assignment.dto.StatusUpdateDTO;
import com.esprit.microservice.assignment.dto.VehicleDTO;
import com.esprit.microservice.assignment.model.Assignment;
import com.esprit.microservice.assignment.model.AssignmentStatus;
import com.esprit.microservice.assignment.repository.AssignmentRepository;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Transactional
public class AssignmentService {

    private static final String AVAILABLE = "AVAILABLE";
    private static final List<AssignmentStatus> ACTIVE_STATUSES =
            List.of(AssignmentStatus.ASSIGNED, AssignmentStatus.IN_PROGRESS);

    private static final Map<AssignmentStatus, Set<AssignmentStatus>> ALLOWED_TRANSITIONS = Map.of(
            AssignmentStatus.ASSIGNED, EnumSet.of(AssignmentStatus.IN_PROGRESS, AssignmentStatus.CANCELLED),
            AssignmentStatus.IN_PROGRESS, EnumSet.of(AssignmentStatus.COMPLETED, AssignmentStatus.CANCELLED)
    );

    private static final Set<AssignmentStatus> TERMINAL_STATUSES = EnumSet.of(
            AssignmentStatus.COMPLETED,
            AssignmentStatus.CANCELLED
    );

    private final AssignmentRepository repository;
    private final DriverClient driverClient;
    private final VehicleClient vehicleClient;
    private final DeliveryClient deliveryClient;
    private final AssignmentEventPublisher eventPublisher;

    public AssignmentService(AssignmentRepository repository,
                             DriverClient driverClient,
                             VehicleClient vehicleClient,
                             DeliveryClient deliveryClient,
                             AssignmentEventPublisher eventPublisher) {
        this.repository = repository;
        this.driverClient = driverClient;
        this.vehicleClient = vehicleClient;
        this.deliveryClient = deliveryClient;
        this.eventPublisher = eventPublisher;
    }

    @Transactional(readOnly = true)
    public List<AssignmentResponseDTO> getAllAssignments() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public AssignmentResponseDTO getAssignmentById(Long id) {
        return toResponse(getOrThrow(id));
    }

    /**
     * Feign scenario 2 (synchronous composition): aggregates the assignment with
     * the driver, vehicle and delivery fetched live from the other services.
     */
    @Transactional(readOnly = true)
    public AssignmentDetailsDTO getAssignmentDetails(Long id) {
        Assignment assignment = getOrThrow(id);
        return new AssignmentDetailsDTO(
                toResponse(assignment),
                fetchDriver(assignment.getDriverId()),
                fetchVehicle(assignment.getVehicleId()),
                fetchDelivery(assignment.getDeliveryId())
        );
    }

    /**
     * Feign scenario 1 (synchronous validation): before creating an assignment the
     * delivery, driver and vehicle are validated with blocking calls to their services.
     * RabbitMQ scenario 1: once saved, an assignment.created event is published
     * so delivery-service marks the delivery ASSIGNED asynchronously.
     */
    public AssignmentResponseDTO createAssignment(AssignmentDTO dto) {
        // Any delivery status is assignable; only the existence of the delivery is
        // validated (there is no UI to reset a delivery back to PENDING).
        fetchDelivery(dto.getDeliveryId());
        if (repository.existsByDeliveryIdAndStatusNot(dto.getDeliveryId(), AssignmentStatus.CANCELLED)) {
            throw badRequest("Delivery already has an active assignment: " + dto.getDeliveryId());
        }

        DriverDTO driver = fetchDriver(dto.getDriverId());
        if (!AVAILABLE.equalsIgnoreCase(driver.status())) {
            throw badRequest("Driver is not available: " + dto.getDriverId());
        }
        if (repository.existsByDriverIdAndStatusIn(dto.getDriverId(), ACTIVE_STATUSES)) {
            throw badRequest("Driver already has an active assignment: " + dto.getDriverId());
        }

        VehicleDTO vehicle = fetchVehicle(dto.getVehicleId());
        if (!AVAILABLE.equalsIgnoreCase(vehicle.status())) {
            throw badRequest("Vehicle is not available: " + dto.getVehicleId());
        }
        if (repository.existsByVehicleIdAndStatusIn(dto.getVehicleId(), ACTIVE_STATUSES)) {
            throw badRequest("Vehicle already has an active assignment: " + dto.getVehicleId());
        }

        Assignment assignment = new Assignment();
        assignment.setDeliveryId(dto.getDeliveryId());
        assignment.setDriverId(dto.getDriverId());
        assignment.setVehicleId(dto.getVehicleId());
        assignment.setStatus(AssignmentStatus.ASSIGNED);
        assignment.setAssignedAt(LocalDateTime.now());
        assignment.setUpdatedAt(LocalDateTime.now());

        Assignment saved = repository.save(assignment);

        eventPublisher.publishAssignmentCreated(new AssignmentCreatedEvent(
                saved.getId(),
                saved.getDeliveryId(),
                saved.getDriverId(),
                saved.getVehicleId(),
                saved.getAssignedAt()
        ));

        return toResponse(saved);
    }

    public AssignmentResponseDTO updateAssignment(Long id, AssignmentDTO dto) {
        Assignment assignment = getOrThrow(id);
        if (assignment.getStatus() != AssignmentStatus.ASSIGNED) {
            throw badRequest("Assignment can only be edited while ASSIGNED: " + id);
        }

        if (!dto.getDriverId().equals(assignment.getDriverId())) {
            DriverDTO driver = fetchDriver(dto.getDriverId());
            if (!AVAILABLE.equalsIgnoreCase(driver.status())) {
                throw badRequest("Driver is not available: " + dto.getDriverId());
            }
            if (repository.existsByDriverIdAndStatusIn(dto.getDriverId(), ACTIVE_STATUSES)) {
                throw badRequest("Driver already has an active assignment: " + dto.getDriverId());
            }
        }

        if (!dto.getVehicleId().equals(assignment.getVehicleId())) {
            VehicleDTO vehicle = fetchVehicle(dto.getVehicleId());
            if (!AVAILABLE.equalsIgnoreCase(vehicle.status())) {
                throw badRequest("Vehicle is not available: " + dto.getVehicleId());
            }
            if (repository.existsByVehicleIdAndStatusIn(dto.getVehicleId(), ACTIVE_STATUSES)) {
                throw badRequest("Vehicle already has an active assignment: " + dto.getVehicleId());
            }
        }

        if (!dto.getDeliveryId().equals(assignment.getDeliveryId())) {
            fetchDelivery(dto.getDeliveryId());
        }

        assignment.setDeliveryId(dto.getDeliveryId());
        assignment.setDriverId(dto.getDriverId());
        assignment.setVehicleId(dto.getVehicleId());
        assignment.setUpdatedAt(LocalDateTime.now());
        return toResponse(repository.save(assignment));
    }

    /**
     * RabbitMQ scenario 2: every status change is published as an
     * assignment.status.changed event; delivery-service consumes it and keeps
     * the delivery status in sync asynchronously.
     */
    public AssignmentResponseDTO updateStatus(Long id, StatusUpdateDTO statusUpdate) {
        Assignment assignment = getOrThrow(id);
        AssignmentStatus previous = assignment.getStatus();
        validateTransition(previous, statusUpdate.getStatus());
        assignment.setStatus(statusUpdate.getStatus());
        assignment.setUpdatedAt(LocalDateTime.now());
        Assignment saved = repository.save(assignment);

        eventPublisher.publishStatusChanged(new AssignmentStatusChangedEvent(
                saved.getId(),
                saved.getDeliveryId(),
                previous.name(),
                saved.getStatus().name(),
                saved.getUpdatedAt()
        ));

        return toResponse(saved);
    }

    public void deleteAssignment(Long id) {
        Assignment assignment = getOrThrow(id);
        if (TERMINAL_STATUSES.contains(assignment.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Assignment already completed or cancelled: " + id);
        }
        repository.delete(assignment);
    }

    @Transactional(readOnly = true)
    public List<AssignmentResponseDTO> getAssignmentsByDriver(Long driverId) {
        return repository.findByDriverId(driverId).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<AssignmentResponseDTO> getAssignmentsByDelivery(Long deliveryId) {
        return repository.findByDeliveryId(deliveryId).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<AssignmentResponseDTO> getAssignmentsByStatus(AssignmentStatus status) {
        return repository.findByStatus(status).stream().map(this::toResponse).toList();
    }

    private Assignment getOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Assignment not found: " + id));
    }

    private void validateTransition(AssignmentStatus current, AssignmentStatus target) {
        Set<AssignmentStatus> allowed = ALLOWED_TRANSITIONS.get(current);
        if (current == target || TERMINAL_STATUSES.contains(current)
                || allowed == null || !allowed.contains(target)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Invalid status transition from " + current + " to " + target);
        }
    }

    private AssignmentResponseDTO toResponse(Assignment assignment) {
        AssignmentResponseDTO dto = new AssignmentResponseDTO();
        dto.setId(assignment.getId());
        dto.setDeliveryId(assignment.getDeliveryId());
        dto.setDriverId(assignment.getDriverId());
        dto.setVehicleId(assignment.getVehicleId());
        dto.setStatus(assignment.getStatus());
        dto.setAssignedAt(assignment.getAssignedAt());
        dto.setUpdatedAt(assignment.getUpdatedAt());
        return dto;
    }

    private ResponseStatusException badRequest(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }

    private DriverDTO fetchDriver(Long id) {
        try {
            return driverClient.getDriver(id);
        } catch (FeignException.NotFound e) {
            throw badRequest("Driver not found: " + id);
        } catch (FeignException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Driver service unavailable");
        }
    }

    private VehicleDTO fetchVehicle(Long id) {
        try {
            return vehicleClient.getVehicle(id);
        } catch (FeignException.NotFound e) {
            throw badRequest("Vehicle not found: " + id);
        } catch (FeignException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Vehicle service unavailable");
        }
    }

    private DeliveryDTO fetchDelivery(Long id) {
        try {
            return deliveryClient.getDelivery(id);
        } catch (FeignException.NotFound e) {
            throw badRequest("Delivery not found: " + id);
        } catch (FeignException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Delivery service unavailable");
        }
    }
}
