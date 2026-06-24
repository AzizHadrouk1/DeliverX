package org.example.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("assignment", r -> r.path("/assignment/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://ASSIGNMENT-SERVICE"))
                .route("driver-client", r -> r.path("/drivers/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://DRIVER-CLIENT-SERVICE"))
                .route("vehicle", r -> r.path("/vehicles/**")
                        .uri("lb://VEHICLE-SERVICE"))
                .route("delivery", r -> r.path("/deliveries/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://DELIVERY-SERVICE"))
                .route("package", r -> r.path("/packages/**")
                        .uri("lb://PACKAGE-SERVICE"))
                .build();
    }


}
