package com.esprit.microservice.package_mgmt.dto;

import com.esprit.microservice.package_mgmt.enums.PackageStatus;
import jakarta.validation.constraints.NotNull;

public class UpdatePackageStatusDTO {

    @NotNull
    private PackageStatus status;

    private String comment;

    public PackageStatus getStatus() {
        return status;
    }

    public void setStatus(PackageStatus status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
