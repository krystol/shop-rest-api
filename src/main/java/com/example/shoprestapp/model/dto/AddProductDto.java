package com.example.shoprestapp.model.dto;

import java.io.Serializable;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddProductDto implements Serializable {

    @NotNull
    private String name;
    @NotNull
    private String price;
}