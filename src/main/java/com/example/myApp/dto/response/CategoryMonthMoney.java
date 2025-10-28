package com.example.myApp.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CategoryMonthMoney {
    private String categoryName;
    private int month;
    private BigDecimal totalMoney;
}
