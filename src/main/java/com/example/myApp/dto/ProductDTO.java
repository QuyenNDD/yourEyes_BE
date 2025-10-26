package com.example.myApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private String size;
    private String color;
    private String genderTarget;

    public ProductDTO(ProductDTO productDTO){
        this.name = productDTO.getName();
        this.description = productDTO.getDescription();
        this.price = productDTO.getPrice();
        this.category = productDTO.getCategory();
        this.size = productDTO.getSize();
        this.color = productDTO.getColor();
        this.genderTarget = productDTO.getGenderTarget();
    }
}
