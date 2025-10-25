package com.example.myApp.service.serviceImpl;

import com.example.myApp.dto.response.ProductAvailableResponse;
import com.example.myApp.dto.ProductDTO;
import com.example.myApp.dto.response.ProductResponse;
import com.example.myApp.enity.*;
import com.example.myApp.repository.*;
import com.example.myApp.service.ProductService;
import com.example.myApp.service.cloud.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CloudinaryService cloudinaryService;

    @Override
    public Page<Products> getAllProductsActive(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        return productRepository.findAllActive(pageable);
    }

    @Override
    public Page<Products> getAllProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        return productRepository.findAll(pageable);
    }

    @Override
    public ProductResponse getProductById(int id) {
        Products products = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        return new ProductResponse(
                products.getId(),
                products.getName(),
                products.getDescription(),
                products.getStock(),
                products.getPrice(),
                products.getCategory().getName(),
                products.getImageUrl(),
                products.getSize(),
                products.getColor(),
                products.getGenderTarget()
        );
    }

    @Override
    public Page<Products> searchProductByName(String name, int page, int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        return productRepository.searchProductsByName(name, pageable);
    }

    @Override
    public Products addProducts(ProductDTO productDTO, List<MultipartFile> images) {
        try {
            // Tìm category
            Category category = categoryRepository.findByName(productDTO.getCategory())
                    .orElseThrow(() -> new RuntimeException("Category not found"));

            // Tạo thư mục uploads nếu chưa có

            // Tạo đối tượng Products
            Products products = Products.builder()
                    .name(productDTO.getName())
                    .description(productDTO.getDescription())
                    .price(productDTO.getPrice())
                    .stock(0)
                    .category(category)
                    .size(productDTO.getSize())
                    .color(productDTO.getColor())
                    .genderTarget(productDTO.getGenderTarget())
                    .createdAt(LocalDateTime.now())
                    .build();

            if (images != null && !images.isEmpty()) {
                List<String> productImageUrls = new ArrayList<>();
                for (MultipartFile img : images) {
                    String url = cloudinaryService.upload(img);
                    productImageUrls.add(url);
                }
                products.setImageUrl(String.join(";", productImageUrls));
            }
            return productRepository.save(products);

        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi thêm sản phẩm: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteProduct(int id){;
        productRepository.deleteProduct(id);
    }

    @Override
    public void restoreProduct(int id){
        productRepository.restoreProduct(id);
    }

    @Override
    public ProductAvailableResponse checkProductAvailable(int productId){
        Products products = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return new ProductAvailableResponse(productId, products.getStock());
    }

    @Override
    public List<Products> filterProducts(Integer categoryId, BigDecimal minPrice, BigDecimal maxPrice, String color, String genderTarget) {
        return productRepository.findByFilters(categoryId, minPrice, maxPrice, color, genderTarget);
    }
}
