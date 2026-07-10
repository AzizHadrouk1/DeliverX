package com.esprit.microservice.delivery.dto;

import jakarta.validation.constraints.NotBlank;

public class DeliveryProofDTO {

    @NotBlank
    private String photoUrl;

    @NotBlank
    private String signature;

    @NotBlank
    private String recipientName;

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }
}
