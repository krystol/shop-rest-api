package com.example.shoprestapp.service;
import com.example.shoprestapp.model.entity.OrderDetailEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;

import com.example.shoprestapp.mapper.Mapper;
import com.example.shoprestapp.model.dto.AddProductDto;
import com.example.shoprestapp.model.dto.OrderDto;
import com.example.shoprestapp.model.entity.OrderEntity;
import com.example.shoprestapp.model.entity.ProductEntity;
import com.example.shoprestapp.repository.OrderJpaRepository;
import com.example.shoprestapp.repository.ProductJpaRepository;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.jpa.domain.Specification;

@RunWith(MockitoJUnitRunner.class)
public class ShopServiceTest {

    @Mock
    private ProductJpaRepository productJpaRepository;
    @Mock
    private OrderJpaRepository orderJpaRepository;
    @Mock
    private Mapper mapper;
    @InjectMocks
    private ShopService shopService;

    @Test
    public void addProductFlowTest() {
        //given
        ProductEntity tomatoProductEntity = createTomatoProductEntity();

        given(mapper.toEntity(any(AddProductDto.class))).willReturn(tomatoProductEntity);
        given(mapper.toDto(any(ProductEntity.class))).willReturn(createTomatoProductDto());
        given(productJpaRepository.save(any(ProductEntity.class))).willReturn(tomatoProductEntity);

        //when
        shopService.addProduct(mockAddProductDto());

        //then
        then(mapper).should().toEntity(any(AddProductDto.class));
        then(mapper).should().toDto(any(ProductEntity.class));
        then(productJpaRepository).should().save(any(ProductEntity.class));
    }

    @Test
    public void updateProductIsSuccess() {
        //given
        ProductEntity tomatoProductEntity = createTomatoProductEntity();

        given(productJpaRepository.findById(any(BigInteger.class))).willReturn(Optional.of(new ProductEntity()));
        given(mapper.toEntity(any(AddProductDto.class))).willReturn(tomatoProductEntity);
        given(productJpaRepository.save(any(ProductEntity.class))).willReturn(tomatoProductEntity);

        //when
        shopService.updateProduct(new BigInteger("1"), new AddProductDto());

        //then
        ArgumentCaptor<ProductEntity> captor = ArgumentCaptor.forClass(ProductEntity.class);
        verify(productJpaRepository).save(captor.capture());

        ProductEntity result = captor.getValue();
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(tomatoProductEntity.getName());
        assertThat(result.getPrice()).isEqualTo(tomatoProductEntity.getPrice());

        then(mapper).should().toDto(any(ProductEntity.class));
    }

    @Test
    public void retrieveProducts() {
        //given
        given(productJpaRepository.findAll()).willReturn(new ArrayList<>());

        //when
        List<ProductEntity> productEntities = shopService.retrieveProducts();

        //then
        assertThat(productEntities).isNotNull().isEmpty();
    }

    @Test
    public void retrieveAllOrdersBetween() {
        //given
        given(orderJpaRepository.findAll(any(Specification.class))).willReturn(new ArrayList<>());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, -1);
        Date monthBefore = calendar.getTime();

        //when
        shopService.retrieveAllOrdersBetween(monthBefore, new Date());

        //then
        then(orderJpaRepository).should().findAll(any(Specification.class));
    }

    @Test
    public void recalculateOrder() {
        //given
        OrderEntity orderEntity = createOrderEntity();
        BigDecimal totalValue = BigDecimal.valueOf(orderEntity.getPurchasedProducts().stream().mapToInt(OrderDetailEntity::getQuantity).sum()).multiply(BigDecimal.TEN);
        
        given(orderJpaRepository.findById(any(BigInteger.class))).willReturn(Optional.of(orderEntity));
        given(productJpaRepository.findAll()).willReturn(createAllProductListWithPricesEqualToTen());
        given(orderJpaRepository.save(any(OrderEntity.class))).willReturn(orderEntity);
        
        //when
        shopService.recalculateOrder(BigInteger.ONE);

        //then
        ArgumentCaptor<OrderEntity> captor = ArgumentCaptor.forClass(OrderEntity.class);
        verify(orderJpaRepository).save(captor.capture());

        OrderEntity result = captor.getValue();
        assertThat(result).isNotNull();
        assertThat(result.getTotalValue()).isEqualTo(totalValue);

        then(productJpaRepository).should().findAll();
        then(mapper).should().toDto(any(OrderEntity.class));

    }

    @Test
    public void addOrder() {
        //given
        OrderEntity orderEntity = createOrderEntity();
        given(mapper.toEntity(any(OrderDto.class))).willReturn(orderEntity);
        given(mapper.toDto(any(OrderEntity.class))).willReturn(new OrderDto());
        given(orderJpaRepository.save(any(OrderEntity.class))).willReturn(orderEntity);
        given(productJpaRepository.findAll()).willReturn(createAllProductListWithPricesEqualToTen());

        //when
        shopService.addOrder(new OrderDto());

        //then
        then(mapper).should().toEntity(any(OrderDto.class));
        then(productJpaRepository).should().findAll();
        then(orderJpaRepository).should().save(any(OrderEntity.class));
        then(mapper).should().toDto(any(OrderEntity.class));
    }

    private List<ProductEntity> createAllProductListWithPricesEqualToTen() {
        ProductEntity entity = createTomatoProductEntity();
        entity.setPrice(BigDecimal.TEN);

        ProductEntity entityTwo = new ProductEntity();
        entityTwo.setId(BigInteger.valueOf(2));
        entityTwo.setPrice(BigDecimal.TEN);
        entityTwo.setName("Mustard");

        List<ProductEntity> listToReturn = new ArrayList<>();
        listToReturn.add(entity);
        listToReturn.add(entityTwo);
        return listToReturn;
    }

    private OrderEntity createOrderEntity() {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderNumber(BigInteger.ONE);
        orderEntity.setPurchasedProducts(createPurchasedProductsList());
        orderEntity.setTotalValue(new BigDecimal("0"));
        orderEntity.setOrderDate(Calendar.getInstance().getTime());
        return orderEntity;
    }

    private List<OrderDetailEntity> createPurchasedProductsList() {
        OrderDetailEntity detail1 = new OrderDetailEntity();
        detail1.setQuantity(2);
        detail1.setProductId(BigInteger.ONE);
        OrderDetailEntity detail2 = new OrderDetailEntity();
        detail2.setQuantity(1);
        detail2.setProductId(BigInteger.valueOf(2));
        ArrayList<OrderDetailEntity> list = new ArrayList();

        list.add(detail1);
        list.add(detail2);
        return list;
    }

    private AddProductDto mockAddProductDto() {
        return new AddProductDto();
    }

    private AddProductDto createTomatoProductDto() {
        AddProductDto dto = new AddProductDto();
        dto.setName("Tomato sauce");
        dto.setPrice("2.59");
        return dto;
    }

    private ProductEntity createTomatoProductEntity() {
        ProductEntity productEntity = new ProductEntity();
        productEntity.setId(BigInteger.ONE);
        productEntity.setName("Tomato sauce");
        productEntity.setPrice(BigDecimal.valueOf(2.59));
        return productEntity;
    }
}