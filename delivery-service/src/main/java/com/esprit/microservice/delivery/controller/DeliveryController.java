package com.esprit.microservice.delivery.controller;

import com.esprit.microservice.delivery.client.PackageClient;
import com.esprit.microservice.delivery.dto.PackageDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class DeliveryController {

    private final PackageClient packageClient;

    public DeliveryController(PackageClient packageClient) {
        this.packageClient = packageClient;
    }

    @GetMapping("/package/{id}")
    public Map<String, Object> getDeliveryWithPackage(@PathVariable Long id) {
        PackageDTO pkg = packageClient.getPackage(id);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("deliveryService", "DELIVERY-SERVICE");
        response.put("message", "Delivery prepared for package");
        response.put("package", pkg);
        response.put("communication", "OpenFeign -> PACKAGE-SERVICE");
        return response;
    }
}
