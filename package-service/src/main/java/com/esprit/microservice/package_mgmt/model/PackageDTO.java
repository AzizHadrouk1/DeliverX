package com.esprit.microservice.package_mgmt.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PackageDTO {

    private Long id;
    private String trackingNumber;
    private double weight;
    private String destination;
    private String status;
}
