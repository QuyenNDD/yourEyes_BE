package com.example.myApp.dto.response;

import com.example.myApp.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderHistoryResponse {
    private int orderId;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private BigDecimal finalPrice;
    private Map<Integer, Integer> products;
}
