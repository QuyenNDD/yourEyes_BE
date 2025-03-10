package com.example.myApp.dto;

import lombok.Data;

@Data
public class CategoryDTO {
    private String name;

    public CategoryDTO(String name) {
        this.name = name;
    }
}
