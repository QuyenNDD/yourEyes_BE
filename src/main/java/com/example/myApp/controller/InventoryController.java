package com.example.myApp.controller;

import com.example.myApp.dto.InventoryResponse;
import com.example.myApp.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/inventory")
public class InventoryController {
    @Autowired
    private InventoryService inventoryService;

    @GetMapping("/report")
    public ResponseEntity<List<InventoryResponse>> getInventoryReport() {
        List<InventoryResponse> inventoryResponses = inventoryService.getInventoryReport();
        return ResponseEntity.ok(inventoryResponses);
    }
}
