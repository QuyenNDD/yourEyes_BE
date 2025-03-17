package com.example.myApp.controller;

import com.example.myApp.dto.StockImportRequest;
import com.example.myApp.service.StockImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/stock-imports")
@RequiredArgsConstructor
public class StockImportController {
    private final StockImportService stockImportService;

    @PostMapping("/import")
    public ResponseEntity<?> importStock(@RequestBody StockImportRequest stockImportRequest, Principal principal){
        try {
            String employeeEmail = principal.getName(); // Lấy email từ token
            stockImportService.importStock(employeeEmail, stockImportRequest);
            return ResponseEntity.ok("Nhập hàng thành công!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi: " + e.getMessage());
        }
    }
}
