package com.esprit.microservice.package_mgmt.dto;

import com.esprit.microservice.package_mgmt.enums.PackageStatus;

import java.time.LocalDateTime;

public class PackageStatusHistoryDTO {

    private Long id;
    private Long packageId;
    private PackageStatus status;
    private LocalDateTime timestamp;
    private String comment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPackageId() {
        return packageId;
    }

    public void setPackageId(Long packageId) {
        this.packageId = packageId;
    }

    public PackageStatus getStatus() {
        return status;
    }

    public void setStatus(PackageStatus status) {
        this.status = status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
