package org.example.gateway.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI deliverXOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("DeliverX API Gateway")
                        .description("Swagger unifié — sélectionnez un microservice dans le menu déroulant")
                        .version("1.0"));
    }
}
