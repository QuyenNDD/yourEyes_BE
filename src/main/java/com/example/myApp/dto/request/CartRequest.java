package com.example.myApp.dto.request;

import lombok.Data;

@Data
public class CartRequest {
    private int productId;
    private int quantity;
}
