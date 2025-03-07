package com.example.myApp.controller;

import com.example.myApp.enity.Products;
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

    //Lay tat ca san pham
    @GetMapping("/getAll")
    public ResponseEntity<Page<Products>> getAllProducts(@RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10") int size){
        Page<Products> products = productService.getAllProducts(page, size);
        return ResponseEntity.ok(products);
    }
    // Lay san pham bang Id
    @GetMapping("/{id}")
    public ResponseEntity<Products> getProductById(@PathVariable Integer id){
        Products products = productService.getProductById(id);
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
}
