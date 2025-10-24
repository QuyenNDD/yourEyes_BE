package com.example.myApp.service.serviceImpl;

import com.example.myApp.dto.response.ProductAvailableResponse;
import com.example.myApp.dto.ProductDTO;
import com.example.myApp.dto.response.ProductResponse;
import com.example.myApp.enity.*;
import com.example.myApp.repository.*;
import com.example.myApp.service.ProductService;
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
    public Products addProducts(ProductDTO productDTO, MultipartFile image) {
        try {
            // Tìm category
            Category category = categoryRepository.findByName(productDTO.getCategory())
                    .orElseThrow(() -> new RuntimeException("Category not found"));

            // Tạo thư mục uploads nếu chưa có
            String uploadDir = "uploads/";
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            // Tạo tên file duy nhất
            String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
            Path filePath = Paths.get(uploadDir + fileName);

            // Lưu file vào thư mục uploads/
            Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Tạo đối tượng Products
            Products products = Products.builder()
                    .name(productDTO.getName())
                    .description(productDTO.getDescription())
                    .price(productDTO.getPrice())
                    .stock(0)
                    .category(category)
                    .imageUrl(uploadDir + fileName)  // Lưu đường dẫn ảnh
                    .createdAt(LocalDateTime.now())
                    .build();

            return productRepository.save(products);

        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi lưu file ảnh: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi thêm sản phẩm: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteProduct(int id){
        Products products = productRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Product not found"));
        productRepository.delete(products);
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
