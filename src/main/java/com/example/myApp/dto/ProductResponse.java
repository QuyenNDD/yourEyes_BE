package com.example.myApp.dto;

import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class ProductResponse {
    private final int id;
    private final String name;
    private final String description;
    private final int stock;
    private final BigDecimal price;
    private final String category;
    private final String imageUrl;

    public ProductResponse(int id, String name, String description, int stock, BigDecimal price, String category, String imageUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.stock = stock;
        this.price = price;
        this.category = category;
        this.imageUrl = imageUrl;
    }

}
