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
                        .uri("lb://DRIVER-CLIENT-SERVICE"))
                .route("client", r -> r.path("/clients/**")
                        .uri("lb://DRIVER-CLIENT-SERVICE"))
                .route("vehicle", r -> r.path("/vehicles/**")
                        .uri("lb://VEHICLE-SERVICE"))
                .route("delivery", r -> r.path("/deliveries/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://DELIVERY-SERVICE"))
                .route("package", r -> r.path("/packages/**")
                        .uri("lb://PACKAGE-SERVICE"))
                .route("tracking", r -> r.path("/tracking/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://TRACKING-SERVICE"))
                .route("tracking-ws", r -> r.path("/ws/**")
                        .uri("lb://TRACKING-SERVICE"))

                // OpenAPI specs aggregated by Gateway Swagger UI (outside /v3/api-docs to avoid springdoc conflict)
                .route("openapi-assignment", r -> r.path("/aggregated-docs/assignment")
                        .filters(f -> f.setPath("/api-docs"))
                        .uri("lb://ASSIGNMENT-SERVICE"))
                .route("openapi-driver-client", r -> r.path("/aggregated-docs/driver-client")
                        .filters(f -> f.setPath("/api-docs"))
                        .uri("lb://DRIVER-CLIENT-SERVICE"))
                .route("openapi-vehicle", r -> r.path("/aggregated-docs/vehicle")
                        .filters(f -> f.setPath("/api-docs"))
                        .uri("lb://VEHICLE-SERVICE"))
                .route("openapi-delivery", r -> r.path("/aggregated-docs/delivery")
                        .filters(f -> f.setPath("/api-docs"))
                        .uri("lb://DELIVERY-SERVICE"))
                .route("openapi-package", r -> r.path("/aggregated-docs/package")
                        .filters(f -> f.setPath("/api-docs"))
                        .uri("lb://PACKAGE-SERVICE"))
                .route("openapi-tracking", r -> r.path("/aggregated-docs/tracking")
                        .filters(f -> f.setPath("/api-docs"))
                        .uri("lb://TRACKING-SERVICE"))
                .build();
    }
}
