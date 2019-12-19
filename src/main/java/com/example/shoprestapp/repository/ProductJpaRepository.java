package com.example.shoprestapp.repository;

import com.example.shoprestapp.model.entity.ProductEntity;
import java.math.BigInteger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductJpaRepository extends JpaRepository<ProductEntity, BigInteger>, JpaSpecificationExecutor {
    
    ProductEntity findByName(String name);

}
