package com.example.shoprestapp.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.shoprestapp.exception.ProductNotFoundException;
import com.example.shoprestapp.model.dto.AddProductDto;
import com.example.shoprestapp.model.dto.OrderDetailDto;
import com.example.shoprestapp.model.dto.OrderDto;
import com.example.shoprestapp.model.entity.OrderEntity;
import com.example.shoprestapp.model.entity.ProductEntity;
import com.example.shoprestapp.repository.OrderJpaRepository;
import com.example.shoprestapp.repository.ProductJpaRepository;
import com.example.shoprestapp.util.RequestUnmarshaller;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.ap.internal.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Sql(executionPhase = BEFORE_TEST_METHOD, scripts = "classpath:/testsql/shop_controller_before_tests.sql")
@Sql(executionPhase = AFTER_TEST_METHOD, scripts = "classpath:/testsql/shop_controller_after_tests.sql")
public class ShopControllerTest {
    private static final String ADD_ORDER_URL = "/order";
    private static final String ADD_PRODUCT_URL = "/item";
    private static final String UPDATE_PRODUCT_URL = "/item/{id}";
    private static final String GET_PRODUCTS_URL = "/items";
    private static final String GET_ORDERS_BETWEEN_DATES_URL = "/orders/{startdate}/{enddate}";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RequestUnmarshaller requestUnmarshaller;
    
    @Autowired
    private ProductJpaRepository productJpaRepository;
    
    @Autowired
    private OrderJpaRepository orderJpaRepository;
    
    @Test
    public void addProduct() throws Exception {
        //given
        AddProductDto product = createAddProductDto();
        String requestJson = requestUnmarshaller.toJson(product);
        assertThat(productJpaRepository.findAll()).hasSize(2);
        
        //when
        MvcResult result = mockMvc.perform(
                post(ADD_PRODUCT_URL)
                        .content(requestJson)
                        .contentType(APPLICATION_JSON_VALUE)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //then
        assertThat(result).isNotNull();
        assertThat(productJpaRepository.findAll()).isNotEmpty().hasSize(3);
    }

    @Test
    public void updateProduct() throws Exception {
        //given
        AddProductDto updateProductDto = createUpdateProductDto();
        String requestJson = requestUnmarshaller.toJson(updateProductDto);
        
        ProductEntity productEntity = productJpaRepository.findByName(updateProductDto.getName());

        //when
        mockMvc.perform(
                put(UPDATE_PRODUCT_URL, productEntity.getId())
                        .content(requestJson)
                        .contentType(APPLICATION_JSON_VALUE)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        
        //then
        ProductEntity productEntityUpdated = productJpaRepository.findByName(updateProductDto.getName());
        assertThat(productEntityUpdated.getName()).isEqualTo(updateProductDto.getName());
        assertThat(productEntityUpdated.getPrice()).isEqualTo(updateProductDto.getPrice());
    }
    
    @Test
    public void retrieveProducts() throws Exception {
        //given

        //when
        MvcResult result = mockMvc.perform(
                get(GET_PRODUCTS_URL))
                .andExpect(status().isOk())
                .andReturn();

        //then
        assertThat(result).isNotNull();
        List<ProductEntity> deserialize = requestUnmarshaller.deserialize(result.getResponse().getContentAsString(), List.class);
        assertThat(deserialize).hasSize(2);
    }
    
    @Test
    public void retrieveProductsBetweenDates() throws Exception {
        //given
        orderJpaRepository.deleteAll();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        
        Calendar instance = Calendar.getInstance();
        instance.setTime(new Date());
        
        Date today = instance.getTime();
        
        instance.add(Calendar.MONTH, -1);
        Date monthAgo = instance.getTime();
        
        instance.add(Calendar.MONTH, -1);
        Date twoMonthAgo = instance.getTime();
        
        instance.add(Calendar.MONTH, -1);
        Date threeMonthAgo = instance.getTime();
        
        OrderEntity entity = createOrderEntityWithDate(monthAgo);
        orderJpaRepository.save(entity);

        OrderEntity oldOrder = createOrderEntityWithDate(threeMonthAgo);
        orderJpaRepository.save(oldOrder);

        assertThat(orderJpaRepository.count()).isEqualTo(2);
        
        //when
        MvcResult result = mockMvc.perform(
                get(GET_ORDERS_BETWEEN_DATES_URL, formatter.format(twoMonthAgo), formatter.format(today)))
                .andExpect(status().isOk())
                .andReturn();

        //then
        assertThat(result).isNotNull();
        List<OrderEntity> deserialize = requestUnmarshaller.deserialize(result.getResponse().getContentAsString(), List.class);
        assertThat(deserialize).isNotEmpty().hasSize(1);
        
    }

    @Test
    public void recalculateOrder() throws Exception {  //fixme zwraca niewypelniony response
        //given
        OrderDto orderDto = createOrderDto();
        BigDecimal totalValueBeforeRecalculating = calculateTotalValue(orderDto);
        String requestJson = requestUnmarshaller.toJson(orderDto);
        assertThat(orderJpaRepository.findAll()).isEmpty();

        //when
        MvcResult result = mockMvc.perform(
                post(ADD_ORDER_URL)
                        .content(requestJson)
                        .contentType(APPLICATION_JSON_VALUE)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //then
        assertThat(result).isNotNull();
        assertThat(orderJpaRepository.findAll()).isNotEmpty().hasSize(1);
    }

    @Test
    public void addOrder() throws Exception {  //fixme zwraca niewypelniony response
        //given
        orderJpaRepository.deleteAll();
        OrderDto orderDto = createOrderDto();
        BigDecimal expectedTotalValue = calculateTotalValue(orderDto);
        String requestJson = requestUnmarshaller.toJson(orderDto);
        assertThat(orderJpaRepository.findAll()).isEmpty();
        
        //when
        MvcResult result = mockMvc.perform(
                post(ADD_ORDER_URL)
                        .content(requestJson)
                        .contentType(APPLICATION_JSON_VALUE)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        
        //then
        assertThat(result).isNotNull();
        assertThat(orderJpaRepository.findAll()).isNotEmpty().hasSize(1);

        OrderEntity savedEntity = Collections.first(orderJpaRepository.findAll());
        assertThat(savedEntity.getTotalValue()).isEqualTo(expectedTotalValue);

    }

    private BigDecimal calculateTotalValue(OrderDto orderDto) {
        BigDecimal totalValue = BigDecimal.ZERO;

        List<ProductEntity> allProducts = productJpaRepository.findAll();

        for (OrderDetailDto orderDetail : orderDto.getPurchasedProducts()) {
            BigInteger id = BigInteger.valueOf(orderDetail.getProductId());
            ProductEntity productEntity = allProducts.stream()
                    .filter(e -> e.getId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new ProductNotFoundException("No products with this id " + id));
            BigDecimal multipliedValue = productEntity.getPrice().multiply(BigDecimal.valueOf(orderDetail.getQuantity()));
            totalValue = totalValue.add(multipliedValue);
        }

        return totalValue;
    }

    private OrderEntity createOrderEntityWithDate(Date date) {
        OrderEntity entity = new OrderEntity();
        entity.setOrderDate(date);
        return entity;
    }

    private OrderDto createOrderDto() {
        OrderDto order = new OrderDto();
        order.setPurchasedProducts(createPurchasedProductsList());        
        return order;
    }

    private List<OrderDetailDto> createPurchasedProductsList() {
        List<ProductEntity> all = productJpaRepository.findAll();
        assertThat(all).size().isGreaterThanOrEqualTo(2);
        
        ProductEntity productEntity = all.get(0);
        OrderDetailDto detail1 = new OrderDetailDto();
        detail1.setQuantity(2);
        detail1.setProductId(productEntity.getId().intValue());

        ProductEntity productEntity2 = all.get(1);
        OrderDetailDto detail2 = new OrderDetailDto();
        detail2.setQuantity(1);
        detail2.setProductId(productEntity2.getId().intValue());
        
        ArrayList<OrderDetailDto> list = new ArrayList<>();

        list.add(detail1);
        list.add(detail2);
        return list;
    }

    private AddProductDto createAddProductDto() {
        AddProductDto dto = new AddProductDto();
        dto.setName("Ketchup");
        dto.setPrice("2.99");
        return dto;
    }
    private AddProductDto createUpdateProductDto() {
        AddProductDto dto = new AddProductDto();
        dto.setName("Tomato sauce");
        dto.setPrice("2.99");
        return dto;
    }
}