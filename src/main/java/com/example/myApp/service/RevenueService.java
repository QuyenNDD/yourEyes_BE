package com.example.myApp.service;

import com.example.myApp.dto.response.CategoryMonthMoney;
import com.example.myApp.dto.response.CategoryYearMoney;
import com.example.myApp.dto.response.MonthlyOrderStatus;

import java.util.List;

public interface RevenueService {
    List<CategoryMonthMoney> getRevenueCategoryByMonthAndYear(int month, int year);
    List<CategoryYearMoney> getRevenueCategoryByYear(int year);
    List<MonthlyOrderStatus> getMonthlyOrderStatus(int year);
}
