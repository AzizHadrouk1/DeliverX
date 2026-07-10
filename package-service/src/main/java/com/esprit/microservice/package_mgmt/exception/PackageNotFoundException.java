package com.esprit.microservice.package_mgmt.exception;

<<<<<<< Updated upstream
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PackageNotFoundException extends RuntimeException {

    public PackageNotFoundException(Long id) {
        super("Package not found with id: " + id);
=======
public class PackageNotFoundException extends RuntimeException {

    public PackageNotFoundException(Long id) {
        super("Package not found: " + id);
>>>>>>> Stashed changes
    }

    public PackageNotFoundException(String trackingNumber) {
        super("Package not found with tracking number: " + trackingNumber);
    }
}
