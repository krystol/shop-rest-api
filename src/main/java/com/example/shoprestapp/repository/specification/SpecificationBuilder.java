package com.example.shoprestapp.repository.specification;

import com.example.shoprestapp.model.entity.OrderEntity;
import java.util.Date;
import org.springframework.data.jpa.domain.Specification;

public class SpecificationBuilder {

    public static Specification<OrderEntity> withOrderDateBetween(final Date startDate, final Date endDate) {
        return Specification
                .where(OrderSpecification.withOrderDateBetween(startDate, endDate));
    }
}