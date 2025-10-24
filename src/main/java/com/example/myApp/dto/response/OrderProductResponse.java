package com.example.myApp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class OrderProductResponse {
    private int productId;
    private String productName;
    private int quantity;
    private BigDecimal price;
    private String imageUrl;
}
