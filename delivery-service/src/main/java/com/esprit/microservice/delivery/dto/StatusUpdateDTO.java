package com.esprit.microservice.delivery.dto;

import com.esprit.microservice.delivery.enums.DeliveryStatus;
import jakarta.validation.constraints.NotNull;

public class StatusUpdateDTO {

    @NotNull
    private DeliveryStatus status;

    private String note;

    public DeliveryStatus getStatus() {
        return status;
    }

    public void setStatus(DeliveryStatus status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
