package com.example.myApp.service;

import com.example.myApp.dto.InventoryResponse;

import java.util.List;

public interface InventoryService {
    List<InventoryResponse> getInventoryReport();
}
