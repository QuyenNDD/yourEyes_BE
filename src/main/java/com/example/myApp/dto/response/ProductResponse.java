package com.example.myApp.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductResponse {
    private final int id;
    private final String name;
    private final String description;
    private final int stock;
    private final BigDecimal price;
    private final String category;
    private final String imageUrl;
    private final String size;
    private final String color;
    private final String genderTarget;

}
