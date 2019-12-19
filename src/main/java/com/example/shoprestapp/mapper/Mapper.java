package com.example.shoprestapp.mapper;

import com.example.shoprestapp.mapper.decorator.IntMapper;
import com.example.shoprestapp.mapper.decorator.MapperDecorator;
import com.example.shoprestapp.model.dto.AddProductDto;
import com.example.shoprestapp.model.dto.OrderDto;
import com.example.shoprestapp.model.entity.OrderEntity;
import com.example.shoprestapp.model.entity.ProductEntity;
import org.mapstruct.DecoratedWith;

@org.mapstruct.Mapper(componentModel = "spring", uses=IntMapper.class)
@DecoratedWith(MapperDecorator.class)
public interface Mapper {

    ProductEntity toEntity(AddProductDto dto);
    AddProductDto toDto(ProductEntity entity);
    OrderEntity toEntity(OrderDto dto);
    OrderDto toDto(OrderEntity entity);
    
}