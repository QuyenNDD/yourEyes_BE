package com.example.myApp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderHistoryResponse {
    private int orderId;
    private String status;
    private LocalDateTime createdAt;
    private BigDecimal finalPrice;
    private Map<Integer, Integer> products;
}
