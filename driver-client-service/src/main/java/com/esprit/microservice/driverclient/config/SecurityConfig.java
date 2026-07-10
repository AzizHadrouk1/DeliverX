package com.esprit.microservice.driverclient.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // ❌ disable CSRF (needed for APIs)
                .csrf(AbstractHttpConfigurer::disable)

                // ❌ stateless (JWT via Keycloak, no server session)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth

                        // health endpoints
                        .requestMatchers("/health", "/drivers/health", "/clients/health", "/hello", "/actuator/**")
                        .permitAll()

                        // self-service: any authenticated client, not just admins, and not
                        // covered by the public-GET/admin-write rules below
                        .requestMatchers(HttpMethod.GET, "/clients/me").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/clients/me").authenticated()

                        // public GET APIs
                        .requestMatchers(HttpMethod.GET, "/drivers/**", "/clients/**")
                        .permitAll()

                        // admin write operations
                        .requestMatchers(HttpMethod.POST, "/drivers/**", "/clients/**").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/drivers/**", "/clients/**").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/drivers/**", "/clients/**").hasAnyRole("ADMIN")

                        // fallback
                        .anyRequest().authenticated()
                )

                // Validate Keycloak-issued JWTs (signature, expiry, issuer) and map client roles
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(keycloakJwtAuthenticationConverter()))
                );

        return http.build();
    }

    // Must match this service's own Keycloak client ID: roles live under
    // resource_access.<this-client-id>.roles in the token, not realm_access.roles.
    private static final String CLIENT_ID = "driver-client-service";

    private Converter<Jwt, AbstractAuthenticationToken> keycloakJwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(this::extractClientRoles);
        // Use Keycloak's human-readable username (not the raw "sub" UUID) as the principal name.
        converter.setPrincipalClaimName("preferred_username");
        return converter;
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
