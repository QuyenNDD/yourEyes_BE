package com.example.myApp.service;

import com.example.myApp.enity.Products;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {
    Page<Products> getAllProducts(int page, int size);
    Products getProductById(Integer id);
    Page<Products> searchProductByName(String name, int page, int size);
}
