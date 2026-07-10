package org.example.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.net.URI;

@Configuration
public class SwaggerRedirectConfig {

    @Bean
    public RouterFunction<ServerResponse> swaggerUiHtmlRedirect() {
        return RouterFunctions.route()
                .GET("/swagger-ui.html", request ->
                        ServerResponse.temporaryRedirect(URI.create("/swagger")).build())
                .build();
    }
}
