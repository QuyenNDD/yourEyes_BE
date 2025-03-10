package com.example.myApp.service;

import com.example.myApp.dto.ProductDTO;
import com.example.myApp.dto.ProductResponse;
import com.example.myApp.enity.Products;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {
    Page<Products> getAllProducts(int page, int size);
    ProductResponse getProductById(int id);
    Page<Products> searchProductByName(String name, int page, int size);
    Products addProducts(ProductDTO productDTO);
    void deleteProduct(int id);
}

