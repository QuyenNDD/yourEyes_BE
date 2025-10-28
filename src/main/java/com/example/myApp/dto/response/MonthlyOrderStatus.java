package com.example.myApp.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MonthlyOrderStatus {
    private int month;
    private int totalOrder;
}
