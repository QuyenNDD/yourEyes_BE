package com.example.myApp.service;

import com.example.myApp.dto.response.CategoryMonthMoney;

import java.util.List;

public interface RevenueService {
    List<CategoryMonthMoney> getRevenueCategoryByMonthAndYear(int month, int year);
}
