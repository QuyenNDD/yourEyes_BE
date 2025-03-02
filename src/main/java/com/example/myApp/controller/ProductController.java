package com.example.myApp.controller;

import com.example.myApp.enity.Products;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<Products> getAllProducts(){
        return productService.getAllProducts();
    }
    // Lay san pham bang Id
    @GetMapping("/{id}")
    public ResponseEntity<Products> getProductById(@PathVariable Integer id){
        Products products = productService.getProductById(id);
        return (products != null) ? ResponseEntity.ok(products) : ResponseEntity.notFound().build();
    }

    // Tim kiem san pham theo ten ( gan giong )
    @GetMapping("/search")
    public List<Products> searchProducts(@RequestParam(value = "name", required = false, defaultValue = "") String name){
        return productService.searchProductByName(name);
    }
}
