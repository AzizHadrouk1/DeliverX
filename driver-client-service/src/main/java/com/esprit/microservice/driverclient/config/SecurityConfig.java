package com.esprit.microservice.driverclient.config;

import com.esprit.microservice.driverclient.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // ❌ disable CSRF (needed for APIs + H2 console)
                .csrf(AbstractHttpConfigurer::disable)

                // ❌ allow H2 console in iframe
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin())
                )

                // ❌ stateless (JWT)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth

                        // ✅ H2 CONSOLE (THIS WAS MISSING)
                        .requestMatchers("/h2-console/**").permitAll()

                        // health endpoints
                        .requestMatchers("/health", "/drivers/health", "/clients/health", "/hello", "/actuator/**")
                        .permitAll()

                        // public GET APIs
                        .requestMatchers(HttpMethod.GET, "/drivers/**", "/clients/**", "/api/analytics/**")
                        .permitAll()

                        // audit protected
                        .requestMatchers(HttpMethod.GET, "/api/audit/**").hasAnyRole("ADMIN")

                        // files protected
                        .requestMatchers("/api/files/**").authenticated()

                        // admin write operations
                        .requestMatchers(HttpMethod.POST, "/drivers/**", "/clients/**").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/drivers/**", "/clients/**").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/drivers/**", "/clients/**").hasAnyRole("ADMIN")

                        // fallback
                        .anyRequest().authenticated()
                )

                // JWT filter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}