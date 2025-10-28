package com.example.myApp.controller;

import com.example.myApp.dto.response.CategoryMonthMoney;
import com.example.myApp.service.RevenueService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/revenue")
public class RevenueController {
    @Autowired
    private RevenueService revenueService;

    @GetMapping("/categoryMonthMoney")
    public ResponseEntity<?> getCategoryMonthMoney(@RequestParam int year,
                                                   @RequestParam int month,
                                                   HttpServletRequest request) {
        Integer roleId = (Integer) request.getAttribute("roleId");
        if (roleId != 2 || roleId == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bạn không có quyền");
        }
        List<CategoryMonthMoney> data = revenueService.getRevenueCategoryByMonthAndYear(month, year);
        return ResponseEntity.ok(data);
    }
}
