package com.example.shoprestapp.repository;

import com.example.shoprestapp.model.entity.OrderEntity;
import java.math.BigInteger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderJpaRepository extends JpaRepository<OrderEntity, BigInteger>, JpaSpecificationExecutor {

}
