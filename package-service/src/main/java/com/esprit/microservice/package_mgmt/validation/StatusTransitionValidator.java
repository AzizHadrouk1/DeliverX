package com.esprit.microservice.package_mgmt.validation;

import com.esprit.microservice.package_mgmt.enums.PackageStatus;
import com.esprit.microservice.package_mgmt.exception.InvalidStatusTransitionException;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

@Component
public class StatusTransitionValidator {

    private static final Map<PackageStatus, Set<PackageStatus>> ALLOWED_TRANSITIONS = Map.of(
            PackageStatus.CREATED, EnumSet.of(PackageStatus.PICKED_UP, PackageStatus.FAILED, PackageStatus.RETURNED),
            PackageStatus.PICKED_UP, EnumSet.of(PackageStatus.IN_TRANSIT, PackageStatus.FAILED, PackageStatus.RETURNED),
            PackageStatus.IN_TRANSIT, EnumSet.of(PackageStatus.OUT_FOR_DELIVERY, PackageStatus.FAILED, PackageStatus.RETURNED),
            PackageStatus.OUT_FOR_DELIVERY, EnumSet.of(PackageStatus.DELIVERED, PackageStatus.FAILED, PackageStatus.RETURNED)
    );

    private static final Set<PackageStatus> TERMINAL_STATUSES = EnumSet.of(
            PackageStatus.DELIVERED,
            PackageStatus.FAILED,
            PackageStatus.RETURNED
    );

    public void validate(PackageStatus current, PackageStatus target) {
        if (current == target) {
            throw new InvalidStatusTransitionException(current, target);
        }
        if (TERMINAL_STATUSES.contains(current)) {
            throw new InvalidStatusTransitionException(current, target);
        }
        Set<PackageStatus> allowed = ALLOWED_TRANSITIONS.get(current);
        if (allowed == null || !allowed.contains(target)) {
            throw new InvalidStatusTransitionException(current, target);
        }
    }

    public boolean isTerminal(PackageStatus status) {
        return TERMINAL_STATUSES.contains(status);
    }
}
