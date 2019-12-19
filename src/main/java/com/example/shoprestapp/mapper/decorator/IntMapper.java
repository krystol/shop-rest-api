package com.example.shoprestapp.mapper.decorator;

import java.math.BigInteger;

public class IntMapper {

    public BigInteger map(int value) {
        return BigInteger.valueOf(value);
    }

    public int map(BigInteger value) {
        return value.intValue();
    }
}
