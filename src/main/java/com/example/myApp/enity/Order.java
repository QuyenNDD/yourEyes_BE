package com.example.myApp.enity;

import com.example.myApp.converter.OrderStatusConverter;
import com.example.myApp.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "total_price", nullable = false,  precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Convert(converter = OrderStatusConverter.class)
    @Column(name = "status", length = 50)
    private OrderStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "discount_id")
    private Discount discount;

    @Column(name = "final_price",  precision = 38, scale = 2)
    private BigDecimal finalPrice;

    @Column(name = "isPaid")
    private Boolean isPaid;

    @Column(name = "paidAt")
    private LocalDateTime paidAt;
}
