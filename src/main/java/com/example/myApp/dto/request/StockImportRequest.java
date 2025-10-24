package com.example.myApp.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockImportRequest {
    @NotBlank(message = "Product name is required")
    private String productName;
    @NotBlank(message = "Quantity is required")
    private int quantity;
}
