package com.example.shoprestapp.repository.specification;

import com.example.shoprestapp.model.entity.OrderEntity;
import java.util.Date;
import org.springframework.data.jpa.domain.Specification;

public class OrderSpecification {

    public static Specification<OrderEntity> withOrderDateBetween(final Date startDate, final Date endDate) {
        if (startDate == null || endDate == null) {
            return null;
        }
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.between(root.get("orderDate"), startDate, endDate);
    }
}