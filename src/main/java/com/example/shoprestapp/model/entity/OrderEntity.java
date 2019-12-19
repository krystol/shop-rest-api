package com.example.shoprestapp.model.entity;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger orderNumber;

    @OneToMany(targetEntity = OrderDetailEntity.class, cascade=CascadeType.ALL, fetch = FetchType.EAGER)
    private List<OrderDetailEntity> purchasedProducts;
    private BigDecimal totalValue;
    private Date orderDate;
}