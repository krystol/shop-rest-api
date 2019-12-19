package com.example.shoprestapp.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.ok;

import com.example.shoprestapp.model.dto.OrderDto;
import com.example.shoprestapp.model.dto.AddProductDto;
import com.example.shoprestapp.service.ShopService;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShopController {

    @Autowired
    private ShopService shopService;

    @PostMapping(path = "/item", consumes = {APPLICATION_JSON_VALUE})
    public ResponseEntity addProduct(@RequestBody AddProductDto dto) {
        return ok(shopService.addProduct(dto));
    }

    @PutMapping(path = "/item/{id}", consumes = {APPLICATION_JSON_VALUE})
    public ResponseEntity updateProduct(@PathVariable(name = "id") final BigInteger id, @RequestBody AddProductDto dto) {
        return ok(shopService.updateProduct(id, dto));
    }

    @GetMapping("/items")
    public ResponseEntity retrieveProducts() {
        return ok(shopService.retrieveProducts());
    }

    @GetMapping("/orders/{startdate}/{enddate}")
    public ResponseEntity retrieveOrdersBetweenDates(
            @PathVariable(name = "startdate")
            @DateTimeFormat(pattern = "yyyy-MM-dd")
                    Date startDate,
            @PathVariable(name = "enddate")
            @DateTimeFormat(pattern = "yyyy-MM-dd")
                    Date endDate) {
        return ok(shopService.retrieveAllOrdersBetween(startDate, endDate));
    }

    @PostMapping(path = "/order", consumes = {APPLICATION_JSON_VALUE})
    public ResponseEntity addOrder(@RequestBody OrderDto dto) {
        return ok(shopService.addOrder(dto));
    }

    @PostMapping(path = "/order/{id}")
    public ResponseEntity recalculateOrder(@PathVariable(name = "id") final BigInteger id) {
        return ok(shopService.recalculateOrder(id));
    }

}