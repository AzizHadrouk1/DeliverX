package com.esprit.microservice.package_mgmt.repository;

import com.esprit.microservice.package_mgmt.entity.PackageEntity;
import com.esprit.microservice.package_mgmt.enums.PackageStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class PackageSpecifications {

    private PackageSpecifications() {
    }

    public static Specification<PackageEntity> withFilters(PackageStatus status, Long clientId) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (clientId != null) {
                predicates.add(cb.equal(root.get("clientId"), clientId));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
