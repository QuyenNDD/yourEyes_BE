package com.example.myApp.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
public class PlaceOrderRequest {
    private List<OrderItemRequest> items; // Danh sách các ID giỏ hàng đã chọn
    private String discountCode;
}
