package com.example.myApp.service.serviceImpl;

import com.example.myApp.dto.StockImportRequest;
import com.example.myApp.enity.Products;
import com.example.myApp.enity.StockImport;
import com.example.myApp.enity.User;
import com.example.myApp.repository.ProductRepository;
import com.example.myApp.repository.StockImportRepository;
import com.example.myApp.repository.UserRepository;
import com.example.myApp.service.StockImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockImportServiceImpl implements StockImportService {
    private final StockImportRepository stockImportRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    public void importStock(String employeeEmail, StockImportRequest stockImportRequests) {
        User employee = userRepository.findByEmail(employeeEmail)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        if (!employee.getRole().getName().equals("EMPLOYEE") && !employee.getRole().getName().equals("ADMIN")) {
            throw new RuntimeException("Employee does not have permission to import stock");
        }

        Products products = productRepository.findByName(stockImportRequests.getProductName())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        StockImport stockImport = StockImport.builder()
                .products(products)
                .importQuantity(stockImportRequests.getQuantity())
                .importDate(LocalDateTime.now())
                .user(employee)
                .build();
        stockImportRepository.save(stockImport);
    }
}
