package com.esprit.microservice.assignment.client;

import com.esprit.microservice.assignment.dto.DeliveryDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "DELIVERY-SERVICE")
public interface DeliveryClient {

    @GetMapping("/api/deliveries/{id}")
    DeliveryDTO getDelivery(@PathVariable("id") Long id);
}
