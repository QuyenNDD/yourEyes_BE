package com.example.myApp.service;

import com.example.myApp.dto.DiscountDTO;
import com.example.myApp.dto.request.DiscountRequest;

import java.util.List;

public interface DiscountService {
    List<DiscountDTO> getAvailableDiscounts();
    void addDiscount(DiscountRequest discountRequest);
}
