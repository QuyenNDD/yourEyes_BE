package com.example.myApp.service.serviceImpl;

import com.example.myApp.dto.response.CategoryMonthMoney;
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
}
