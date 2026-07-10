package com.esprit.microservice.tracking.dto;

import com.esprit.microservice.tracking.enums.PackageStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatusUpdateRequest {

    @NotNull(message = "status is required")
    private PackageStatus status;

    private String notes;
}
