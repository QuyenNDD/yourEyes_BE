package com.example.myApp.controller;

import com.example.myApp.dto.request.StockImportRequest;
import com.example.myApp.service.StockImportService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stock-imports")
@RequiredArgsConstructor
public class StockImportController {
    private final StockImportService stockImportService;

    @PostMapping("/import")
    public ResponseEntity<?> importStock(@RequestBody StockImportRequest stockImportRequest, HttpServletRequest request){
        try {
            Integer roleId = (Integer) request.getAttribute("roleId");
            String employeeEmail = (String) request.getAttribute("email");
            if (employeeEmail == null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vui lòng đăng nhập");
            }else if (roleId == null || roleId == 1) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bạn không có quyền");
            }
            stockImportService.importStock(employeeEmail, stockImportRequest);
            return ResponseEntity.ok("Nhập hàng thành công!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi: " + e.getMessage());
        }
    }
}
