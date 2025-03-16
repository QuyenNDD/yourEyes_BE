package com.example.myApp.service;

import com.example.myApp.dto.DiscountDTO;
import com.example.myApp.dto.DiscountRequest;
import com.example.myApp.enity.Discount;

import java.util.List;

public interface DiscountService {
    List<DiscountDTO> getAvailableDiscounts();
    void addDiscount(DiscountRequest discountRequest);
}
