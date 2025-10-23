package com.example.myApp.controller;

import com.example.myApp.dto.ProductAvailableResponse;
import com.example.myApp.dto.ProductDTO;
import com.example.myApp.dto.ProductResponse;
import com.example.myApp.enity.Products;
import com.example.myApp.repository.CategoryRepository;
import com.example.myApp.repository.ProductRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.example.myApp.service.ProductService;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;
    //Lay tat ca san pham
    @GetMapping("/getAll")
    public ResponseEntity<Page<Products>> getAllProducts(@RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10") int size){
        Page<Products> products = productService.getAllProducts(page, size);
        return ResponseEntity.ok(products);
    }
    // Lay san pham bang Id
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable int id){
        ProductResponse products = productService.getProductById(id);
        return (products != null) ? ResponseEntity.ok(products) : ResponseEntity.notFound().build();
    }

    // Tim kiem san pham theo ten ( gan giong )
    @GetMapping("/search")
    public ResponseEntity<Page<Products>> searchProducts(@RequestParam String name,
                                                         @RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10") int size){
        Page<Products> products = productService.searchProductByName(name, page, size);
        return ResponseEntity.ok(products);
    }

    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addProducts(@ModelAttribute ProductDTO productDTO,
                                         @RequestParam("image") MultipartFile image,
                                         HttpServletRequest request){
        try {
            Integer userId = (Integer) request.getAttribute("userId");
            if (userId == null || userId != 2) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bạn không có quyền");
            }
            Products savedProduct = productService.addProducts(productDTO, image);
            return ResponseEntity.ok(savedProduct);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateProducts(
            @PathVariable int id,
            @RequestBody ProductDTO productDTO,
            HttpServletRequest request){
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null || userId != 2) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bạn không có quyền");
        }
        Products products = productRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Not found product with id = " + id));
        products.updateFromDTO(productDTO, categoryRepository);
        productRepository.save(products);
        return ResponseEntity.ok("Update success");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteProducts(@PathVariable int id,
                                                 HttpServletRequest request){
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null || userId != 2) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bạn không có quyền");
        }
        productService.deleteProduct(id);
        return ResponseEntity.ok("Delete success");
    }

    @GetMapping("/{productId}/available")
    public ResponseEntity<ProductAvailableResponse> checkProductAvailable(@PathVariable int productId){
        ProductAvailableResponse productAvailableResponse = productService.checkProductAvailable(productId);
        return ResponseEntity.ok(productAvailableResponse);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Products>> filterProducts(
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) String genderTarget) {

        List<Products> products = productService.filterProducts(categoryId, minPrice, maxPrice, color, genderTarget);
        return ResponseEntity.ok(products);
    }
}
