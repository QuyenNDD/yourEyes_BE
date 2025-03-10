package com.example.myApp.controller;

import com.example.myApp.dto.CategoryDTO;
import com.example.myApp.enity.Category;
import com.example.myApp.repository.CategoryRepository;
import com.example.myApp.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final ProductService productService;
    private final CategoryRepository categoryRepository;

    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategories(){
        List<Category> categories = categoryRepository.findAll();

        List<CategoryDTO> categoryDTOS = categories.stream()
                .map(category -> new CategoryDTO(category.getName()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(categoryDTOS);
    }

}
