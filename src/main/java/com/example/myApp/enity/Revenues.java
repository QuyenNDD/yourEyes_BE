package com.example.myApp.enity;

import jakarta.persistence.*;
import jdk.jfr.DataAmount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "revenues")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Revenues {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "revenue_month", nullable = false)
    private int revenueMonth;

    @Column(name = "revenue_year", nullable = false)
    private int revenueYear;

    @Column(name = "total_orders")
    private int totalOrders;

    @Column(name = "total_revenue", precision = 18, scale = 2)
    private BigDecimal totalRenenue;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

}
