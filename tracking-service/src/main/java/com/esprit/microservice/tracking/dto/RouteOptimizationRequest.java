package com.esprit.microservice.tracking.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteOptimizationRequest {

    @NotBlank(message = "deliveryId is required")
    private String deliveryId;

    @NotEmpty(message = "waypoints must not be empty")
    @Valid
    private List<WaypointRequest> waypoints;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WaypointRequest {
        @NotNull
        private Double latitude;
        @NotNull
        private Double longitude;
        private String label;
    }
}
