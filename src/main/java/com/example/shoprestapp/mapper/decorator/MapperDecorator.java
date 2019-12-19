package com.example.shoprestapp.mapper.decorator;

import com.example.shoprestapp.mapper.Mapper;
import com.example.shoprestapp.model.dto.AddProductDto;
import com.example.shoprestapp.model.dto.OrderDetailDto;
import com.example.shoprestapp.model.dto.OrderDto;
import com.example.shoprestapp.model.entity.OrderDetailEntity;
import com.example.shoprestapp.model.entity.OrderEntity;
import com.example.shoprestapp.model.entity.ProductEntity;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@org.mapstruct.Mapper(componentModel = "spring", uses=IntMapper.class)
public class MapperDecorator implements Mapper {

    @Autowired
    @Qualifier("delegate")
    private Mapper delegate;

    @Override
    public ProductEntity toEntity(AddProductDto dto) {
        ProductEntity productEntity = delegate.toEntity(dto);
        productEntity.setPrice(new BigDecimal(dto.getPrice()));
        return productEntity;
    }

    @Override
    public AddProductDto toDto(ProductEntity entity) {
        return delegate.toDto(entity);
    }

    @Override
    public OrderEntity toEntity(OrderDto dto) {
        OrderEntity orderEntity = delegate.toEntity(dto);
        orderEntity.setPurchasedProducts(dto.getPurchasedProducts()
                .stream()
                .map(this::toEntity)
                .collect(Collectors.toList()));
        return orderEntity;
    }

    @Override
    public OrderDto toDto(OrderEntity entity) {
        return delegate.toDto(entity);
    }
    
    private OrderDetailEntity toEntity(OrderDetailDto dto){
        if(dto == null) {
            return null;
        }
        OrderDetailEntity entity = new OrderDetailEntity();
        entity.setProductId(BigInteger.valueOf(dto.getProductId()));
        entity.setQuantity(dto.getQuantity());
        return entity;
    }
}

