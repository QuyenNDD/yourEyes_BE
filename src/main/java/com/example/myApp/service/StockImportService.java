package com.example.myApp.service;

import com.example.myApp.dto.request.StockImportRequest;

public interface StockImportService {
    void importStock(String employeeEmail, StockImportRequest stockImportRequests);
}
