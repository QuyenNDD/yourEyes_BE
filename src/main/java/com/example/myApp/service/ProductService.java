package com.example.myApp.service;

import com.example.myApp.dto.ProductAvailableResponse;
import com.example.myApp.dto.ProductDTO;
import com.example.myApp.dto.ProductResponse;
import com.example.myApp.enity.Category;
import com.example.myApp.enity.Order;
import com.example.myApp.enity.Products;
import com.example.myApp.enity.User;
import com.example.myApp.enums.OrderStatus;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {
    Page<Products> getAllProducts(int page, int size);
    ProductResponse getProductById(int id);
    Page<Products> searchProductByName(String name, int page, int size);
    Products addProducts(ProductDTO productDTO);
    void deleteProduct(int id);
    ProductAvailableResponse checkProductAvailable(int id);
    List<Products> filterProducts(Integer categoryId, BigDecimal minPrice, BigDecimal maxPrice);
}

