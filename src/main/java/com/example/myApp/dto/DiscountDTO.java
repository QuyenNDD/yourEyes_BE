package com.example.myApp.dto;

import com.example.myApp.enity.Discount;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class DiscountDTO {
    private String code;
    private String description;
    private BigDecimal discountPercentage;
    private LocalDate startDate;
    private LocalDate endDate;
    public DiscountDTO(Discount discount) {
        this.code = discount.getCode();
        this.description = discount.getDescription();
        this.discountPercentage = discount.getDiscountPercentage();
        this.startDate = discount.getStartDate();
        this.endDate = discount.getEndDate();
    }
}
