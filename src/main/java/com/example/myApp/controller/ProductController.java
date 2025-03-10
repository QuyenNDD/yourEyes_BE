package com.example.myApp.controller;

import com.example.myApp.dto.ProductDTO;
import com.example.myApp.dto.ProductResponse;
import com.example.myApp.enity.Products;
import com.example.myApp.repository.CategoryRepository;
import com.example.myApp.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.myApp.service.ProductService;

import java.util.List;

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

    @PostMapping("/add")
    public ResponseEntity<?> addProducts(@RequestBody ProductDTO productDTO){
        try {
            Products products = productService.addProducts(productDTO);
            return ResponseEntity.ok(products);
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateProducts(
            @PathVariable int id,
            @RequestBody ProductDTO productDTO){
        Products products = productRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Not found product with id = " + id));
        products.updateFromDTO(productDTO, categoryRepository);
        productRepository.save(products);
        return ResponseEntity.ok("Update success");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteProducts(@PathVariable int id){
        productService.deleteProduct(id);
        return ResponseEntity.ok("Delete success");
    }
}
