package com.esprit.microservice.package_mgmt.exception;

public class PackageDeleteConflictException extends RuntimeException {

    public PackageDeleteConflictException(Long id, String status) {
        super("Package " + id + " cannot be deleted when status is " + status + ". Only CREATED packages can be deleted.");
    }
}
