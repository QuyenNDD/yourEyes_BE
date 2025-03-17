package com.example.myApp.service;

import com.example.myApp.dto.StockImportRequest;
import com.example.myApp.enity.StockImport;

import java.util.List;

public interface StockImportService {
    void importStock(String employeeEmail, StockImportRequest stockImportRequests);
}
