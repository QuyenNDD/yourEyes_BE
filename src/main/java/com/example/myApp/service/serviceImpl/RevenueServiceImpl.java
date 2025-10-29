package com.example.myApp.service.serviceImpl;

import com.example.myApp.dto.response.CategoryMonthMoney;
import com.example.myApp.dto.response.CategoryYearMoney;
import com.example.myApp.dto.response.MonthlyOrderStatus;
import com.example.myApp.repository.RevenueRepository;
import com.example.myApp.service.RevenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RevenueServiceImpl implements RevenueService {
    private final RevenueRepository revenueRepository;

    public List<CategoryMonthMoney> getRevenueCategoryByMonthAndYear(int month, int year){
        List<Object[]> results = revenueRepository.getCategoryMonthMoney(year, month);
        List<CategoryMonthMoney> list = new ArrayList<>();
        for (Object[] row : results) {
            CategoryMonthMoney categoryMonthMoney = new CategoryMonthMoney();
            categoryMonthMoney.setCategoryName((String) row[0]);
            categoryMonthMoney.setMonth((Integer) row[1]);
            categoryMonthMoney.setTotalMoney((BigDecimal) row[2]);
            list.add(categoryMonthMoney);
        }
        return list;
    }

    public List<CategoryYearMoney> getRevenueCategoryByYear(int year){
        List<Object[]> results = revenueRepository.getCategoryYearMoney(year);
        List<CategoryYearMoney> list = new ArrayList<>();
        for (Object[] row : results) {
            CategoryYearMoney categoryYearMoney = new CategoryYearMoney();
            categoryYearMoney.setCategoryName((String) row[0]);
            categoryYearMoney.setTotalMoney((BigDecimal) row[1]);
            list.add(categoryYearMoney);
        }
        return list;
    }

    public List<MonthlyOrderStatus> getMonthlyOrderStatus(int year){
        List<Object[]> results = revenueRepository.getMonthlyOrderRevenue(year);
        List<MonthlyOrderStatus> list = new ArrayList<>();
        for (Object[] row : results) {
            MonthlyOrderStatus monthlyOrderStatus = new MonthlyOrderStatus();
            monthlyOrderStatus.setMonth((Integer) row[0]);
            monthlyOrderStatus.setTotalOrder((Integer) row[1]);
            list.add(monthlyOrderStatus);
        }
        return list;
    }
}
