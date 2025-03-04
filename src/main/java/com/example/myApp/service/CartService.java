package com.example.myApp.service;

import com.example.myApp.enity.Products;

public interface CartService {
    void addToCart(String email, int productId, int quantity);
}
