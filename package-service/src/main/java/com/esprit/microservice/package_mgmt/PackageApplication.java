package com.esprit.microservice.package_mgmt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class PackageApplication {

    public static void main(String[] args) {
        SpringApplication.run(PackageApplication.class, args);
    }
}
