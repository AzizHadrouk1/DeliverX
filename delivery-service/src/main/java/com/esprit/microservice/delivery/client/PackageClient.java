package com.esprit.microservice.delivery.client;

import com.esprit.microservice.delivery.dto.PackageDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "PACKAGE-SERVICE")
public interface PackageClient {

    @GetMapping("/packages/{id}")
    PackageDTO getPackage(@PathVariable("id") Long id);
}
