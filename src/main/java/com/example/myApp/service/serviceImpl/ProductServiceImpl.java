package com.example.myApp.service.serviceImpl;

import com.example.myApp.dto.ProductAvailableResponse;
import com.example.myApp.dto.ProductDTO;
import com.example.myApp.dto.ProductResponse;
import com.example.myApp.enity.Category;
import com.example.myApp.enity.Inventory;
import com.example.myApp.enity.Products;
import com.example.myApp.repository.CategoryRepository;
import com.example.myApp.repository.InventoryRepository;
import com.example.myApp.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.example.myApp.repository.ProductRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private InventoryRepository inventoryRepository;

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
                products.getCategoryId().getName(),
                products.getImageUrl()
        );
    }

    @Override
    public Page<Products> searchProductByName(String name, int page, int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        return productRepository.searchProductsByName(name, pageable);
    }

    @Override
    public Products addProducts(ProductDTO productDTO) {
        Category category = categoryRepository.findByName(productDTO.getCategory())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        Products products = Products.builder()
                .name(productDTO.getName())
                .description(productDTO.getDescription())
                .price(productDTO.getPrice())
                .stock(0)
                .categoryId(category)
                .imageUrl(productDTO.getImageUrl())
                .createdAt(LocalDateTime.now())
                .build();
        return productRepository.save(products);
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
        Inventory inventory = inventoryRepository.findByProducts(products)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));
        boolean available = inventory.getStockQuantity() > 0;
        return new ProductAvailableResponse(productId, inventory.getStockQuantity());
    }
}
