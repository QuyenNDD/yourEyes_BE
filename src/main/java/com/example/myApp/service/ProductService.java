package com.example.myApp.service;

import com.example.myApp.dto.response.ProductAvailableResponse;
import com.example.myApp.dto.ProductDTO;
import com.example.myApp.dto.response.ProductResponse;
import com.example.myApp.enity.Products;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {
    Page<Products> getAllProducts(int page, int size);
    ProductResponse getProductById(int id);
    Page<Products> searchProductByName(String name, int page, int size);
    Products addProducts(ProductDTO productDTO, MultipartFile image);
    void deleteProduct(int id);
    ProductAvailableResponse checkProductAvailable(int id);
    List<Products> filterProducts(Integer categoryId, BigDecimal minPrice, BigDecimal maxPrice, String color, String genderTarget);
}

