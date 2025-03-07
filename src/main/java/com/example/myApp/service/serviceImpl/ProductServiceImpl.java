package com.example.myApp.service.serviceImpl;

import com.example.myApp.enity.Products;
import com.example.myApp.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.example.myApp.repository.ProductRepository;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Page<Products> getAllProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        return productRepository.findAll(pageable);
    }

    @Override
    public Products getProductById(Integer id) {
        return productRepository.findById(id).orElse(null);
    }

    @Override
    public Page<Products> searchProductByName(String name, int page, int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        return productRepository.searchProductsByName(name, pageable);
    }
}
