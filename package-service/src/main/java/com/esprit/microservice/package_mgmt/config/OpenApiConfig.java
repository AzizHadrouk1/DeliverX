package com.esprit.microservice.package_mgmt.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI packageOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Package Service API")
                        .description("Package lifecycle management — CRUD, tracking, status history")
                        .version("1.0"))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8090")
                                .description("API Gateway"),
                        new Server()
                                .url("http://localhost:8085")
                                .description("Package Service direct")
                ));
    }
}
