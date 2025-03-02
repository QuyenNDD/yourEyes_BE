package com.example.myApp.service;

import com.example.myApp.enity.Products;

import java.util.List;

public interface ProductService {
    List<Products> getAllProducts();
    Products getProductById(Integer id);
    List<Products> searchProductByName(String name);
}
