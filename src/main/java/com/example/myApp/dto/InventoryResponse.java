package com.example.myApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class InventoryResponse {
    private String productName;
    private int quantity;
    private LocalDateTime lastUpdated;
}
