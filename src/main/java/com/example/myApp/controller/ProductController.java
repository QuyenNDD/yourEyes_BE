package com.example.myApp.controller;

import com.example.myApp.enity.Products;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.myApp.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping("/getAll")
    public List<Products> getAllProducts(){
        System.out.println("GET /api/products/getAll called"); // Log kiểm tra
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Products> getProductById(@PathVariable Integer id){
        Products products = productService.getProductById(id);
        return (products != null) ? ResponseEntity.ok(products) : ResponseEntity.notFound().build();
    }
}
