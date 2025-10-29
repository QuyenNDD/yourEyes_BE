package com.example.myApp.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CategoryYearMoney {
    private String categoryName;
    private BigDecimal totalMoney;
}
