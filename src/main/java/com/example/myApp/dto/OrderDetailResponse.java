package com.example.myApp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class OrderDetailResponse {
    private int orderId;
    private BigDecimal finalPrice;
    private String status;
    private List<OrderProductResponse> products;
}
