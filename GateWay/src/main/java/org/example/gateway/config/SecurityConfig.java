package org.example.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Centralized authentication check for every route proxied by the Gateway, per the
 * platform's architecture (Keycloak wired to the Gateway, not to each microservice
 * individually). This intentionally only asserts "is this a real, authenticated
 * Keycloak session" - fine-grained role checks (e.g. admin-only writes) remain the
 * responsibility of each downstream service, which knows its own business rules.
 * Downstream services that validate the token themselves (e.g. driver-client-service)
 * still do so - this is a defense-in-depth layer, not a replacement.
 */
@Configuration
public class SecurityConfig {

    // Only driver-client-service has Keycloak client roles modeled today; its roles
    // double as the platform-wide role source until other services get their own.
    private static final String CLIENT_ID = "driver-client-service";

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        // CORS preflight requests never carry the Authorization header - they
                        // must always be allowed through, or the browser blocks the real request.
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Reads stay public, matching each service's own permitAll GET routes.
                        .pathMatchers(HttpMethod.GET, "/**").permitAll()
                        // Any mutation (POST/PUT/DELETE/PATCH) requires a real Keycloak session.
                        // Which specific role is allowed to do what is still decided downstream.
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(reactiveJwtAuthenticationConverter()))
                );

        return http.build();
    }

    private Converter<Jwt, Mono<AbstractAuthenticationToken>> reactiveJwtAuthenticationConverter() {
        JwtAuthenticationConverter delegate = new JwtAuthenticationConverter();
        delegate.setJwtGrantedAuthoritiesConverter(this::extractClientRoles);
        delegate.setPrincipalClaimName("preferred_username");
        return new ReactiveJwtAuthenticationConverterAdapter(delegate);
    }

    @SuppressWarnings("unchecked")
    private Collection<GrantedAuthority> extractClientRoles(Jwt jwt) {
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
        if (resourceAccess == null || resourceAccess.get(CLIENT_ID) == null) {
            return List.of();
        }

        Map<String, Object> clientAccess = (Map<String, Object>) resourceAccess.get(CLIENT_ID);
        List<String> roles = (List<String>) clientAccess.get("roles");
        if (roles == null) {
            return List.of();
        }

        return roles.stream()
                .map(role -> (GrantedAuthority) new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                .collect(Collectors.toList());
    }
}
