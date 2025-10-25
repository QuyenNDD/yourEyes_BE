package com.example.myApp.dto.request;

import lombok.Data;

@Data
public class OrderItemRequest {
    private int cartItemId;
    private int quantity;
}
