package com.esprit.microservice.package_mgmt.exception;

import com.esprit.microservice.package_mgmt.enums.PackageStatus;

public class InvalidStatusTransitionException extends RuntimeException {

    public InvalidStatusTransitionException(PackageStatus from, PackageStatus to) {
        super("Invalid status transition from " + from + " to " + to);
    }
}
