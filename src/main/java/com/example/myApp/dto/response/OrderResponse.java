package com.example.myApp.dto.response;

import com.example.myApp.enity.Order;
import com.example.myApp.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
    private int orderId;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private String discountCode;
    private BigDecimal discountPercentage;
    private BigDecimal totalPrice;
    private BigDecimal finalPrice; // Lấy từ database

    public OrderResponse(Order order) {
        this.orderId = order.getId();
        this.status = order.getStatus();
        this.createdAt = order.getCreatedAt();
        this.totalPrice = order.getTotalPrice();  // Lấy từ DB
        this.finalPrice = order.getFinalPrice();  // Lấy từ DB

        if (order.getDiscount() != null) {
            this.discountCode = order.getDiscount().getCode();
            this.discountPercentage = order.getDiscount().getDiscountPercentage();
        } else {
            this.discountCode = null;
            this.discountPercentage = BigDecimal.valueOf(0.00);
        }
    }
}
