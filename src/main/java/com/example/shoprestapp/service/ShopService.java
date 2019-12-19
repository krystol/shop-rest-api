package com.example.shoprestapp.service;

import com.example.shoprestapp.exception.OrderNotFoundException;
import com.example.shoprestapp.exception.ProductNotFoundException;
import com.example.shoprestapp.mapper.Mapper;
import com.example.shoprestapp.model.dto.AddProductDto;
import com.example.shoprestapp.model.dto.OrderDto;
import com.example.shoprestapp.model.entity.OrderDetailEntity;
import com.example.shoprestapp.model.entity.OrderEntity;
import com.example.shoprestapp.model.entity.ProductEntity;
import com.example.shoprestapp.repository.OrderJpaRepository;
import com.example.shoprestapp.repository.ProductJpaRepository;
import com.example.shoprestapp.repository.specification.SpecificationBuilder;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class ShopService {

    private static final String NO_PRODUCT_WITH_THIS_ID_MESSAGE = "No product with this id: ";
    private static final String NO_ORDER_WITH_THIS_ID_MESSAGE = "No product with this id: ";
    
    @Autowired
    private ProductJpaRepository productJpaRepository;
    @Autowired
    private OrderJpaRepository orderJpaRepository;
    @Autowired
    private Mapper mapper;

    public AddProductDto addProduct(AddProductDto dto) {
        ProductEntity productEntity = mapper.toEntity(dto);
        return mapper.toDto(productJpaRepository.save(productEntity));
    }

    public AddProductDto updateProduct(BigInteger id, AddProductDto dto) {
        ProductEntity item = productJpaRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(NO_PRODUCT_WITH_THIS_ID_MESSAGE + id));
        
        ProductEntity productEntity = mapper.toEntity(dto);
        
        item.setPrice(productEntity.getPrice());
        item.setName(productEntity.getName());
        
        return mapper.toDto(productJpaRepository.save(item));
    }

    public List<ProductEntity> retrieveProducts() {
        return productJpaRepository.findAll();
    }

    public List<OrderEntity> retrieveAllOrdersBetween(Date startDate, Date endDate) {
        Specification<OrderEntity> orderSpecificationBetweenDates = SpecificationBuilder.withOrderDateBetween(startDate, endDate);
        return orderJpaRepository.findAll(orderSpecificationBetweenDates);
    }

    public OrderDto recalculateOrder(BigInteger orderId) {
        OrderEntity orderEntity = orderJpaRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(NO_ORDER_WITH_THIS_ID_MESSAGE + orderId));
        
        List<OrderDetailEntity> purchasedProducts = orderEntity.getPurchasedProducts();
        BigDecimal totalValue = calculateTotalValue(purchasedProducts);
        
        orderEntity.setTotalValue(totalValue);
        
        return mapper.toDto(orderJpaRepository.save(orderEntity));
    }

    public OrderDto addOrder(OrderDto dto) {
        OrderEntity orderEntity = mapper.toEntity(dto);
        orderEntity.setOrderDate(new Date());
        
        BigDecimal totalValue = calculateTotalValue(orderEntity.getPurchasedProducts());
        orderEntity.setTotalValue(totalValue);
        
        OrderEntity savedOrder = orderJpaRepository.save(orderEntity);
        return mapper.toDto(savedOrder);
    }

    private BigDecimal calculateTotalValue(List<OrderDetailEntity> purchasedProducts) {
        BigDecimal totalValue = BigDecimal.ZERO;

        List<ProductEntity> allProducts = productJpaRepository.findAll();

        for (OrderDetailEntity orderDetail : purchasedProducts) {
            BigInteger id = orderDetail.getProductId();
            ProductEntity productEntity = allProducts.stream()
                    .filter(e -> e.getId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new ProductNotFoundException(NO_PRODUCT_WITH_THIS_ID_MESSAGE + id));
            BigDecimal multipliedValue = productEntity.getPrice().multiply(BigDecimal.valueOf(orderDetail.getQuantity())); 
            totalValue = totalValue.add(multipliedValue);
        }
        
        return totalValue;
    }
}