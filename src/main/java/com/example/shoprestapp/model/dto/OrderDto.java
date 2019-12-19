package com.example.shoprestapp.model.dto;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Data
public class OrderDto implements Serializable {

    private List<OrderDetailDto> purchasedProducts;
    
    //returned fields
    private String orderNumber;
    private String totalValue;
    private String orderDate;

}