package com.esprit.microservice.driverclient.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

/**
 * Shared by DriverService and ClientService to avoid repeating the same
 * "if this value is already taken, reject with 409" check on every create/update.
 */
@Component
public class UniquenessGuard {

    public void check(boolean alreadyExists, String message) {
        if (alreadyExists) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, message);
        }
    }
}
