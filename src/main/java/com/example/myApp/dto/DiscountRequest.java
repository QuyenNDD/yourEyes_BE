package com.example.myApp.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Locale;

@Getter
@Setter
public class DiscountRequest {
    @NotBlank(message = "Không được để trống")
    private String code;

    @NotNull(message = "Phần trăm giảm giá không được để trống")
    @DecimalMin(value = "0.01", message = "Giảm giá phải lớn hơn 0")
    @DecimalMax(value = "100.00", message = "Giảm giá không được vượt quá 100%")
    private String description;
    @NotBlank(message = "Không được để trống")
    private BigDecimal discountPercentage;
    @NotBlank(message = "Không được để trống")
    private LocalDate startDate;
    @NotBlank(message = "Không được để trống")
    private LocalDate endDate;
}
