package com.example.myApp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class ProductDTO {
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private String imageUrl;

    public ProductDTO(ProductDTO productDTO){
        this.name = productDTO.getName();
        this.description = productDTO.getDescription();
        this.price = productDTO.getPrice();
        this.imageUrl = productDTO.getImageUrl();
        this.category = productDTO.getCategory();
    }
}
