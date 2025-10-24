package com.example.myApp.dto.response;

import com.example.myApp.enums.OrderStatus;
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
    private BigDecimal totalPrice;
    private OrderStatus status;
    private List<OrderProductResponse> products;
}
