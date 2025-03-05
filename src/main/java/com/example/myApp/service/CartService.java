package com.example.myApp.service;

import com.example.myApp.enity.Cart;
import com.example.myApp.enity.Products;

import java.util.List;

public interface CartService {
    void addToCart(String email, int productId, int quantity);
    void removeFromCart(String email, int productId);
    List<Cart> getCartByUser(String email);
    void updateCart(String email, int productId, int newQuantiry);
}
