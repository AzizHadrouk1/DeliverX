package com.esprit.microservice.driverclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class DriverClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(DriverClientApplication.class, args);
    }
}
