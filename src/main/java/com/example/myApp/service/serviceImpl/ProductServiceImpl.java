package com.example.myApp.service.serviceImpl;

import com.example.myApp.enity.Products;
import com.example.myApp.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.myApp.repository.ProductRepository;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<Products> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Products getProductById(Integer id) {
        return productRepository.findById(id).orElse(null);
    }

    @Override
    public List<Products> searchProductByName(String name){
        return productRepository.findByNameContaining(name);
    }
}
