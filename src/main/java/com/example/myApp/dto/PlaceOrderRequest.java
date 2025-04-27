package com.example.myApp.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PlaceOrderRequest {
    private List<Integer> cartItemIds; // Danh sách các ID giỏ hàng đã chọn
    private String discountCode;
}
