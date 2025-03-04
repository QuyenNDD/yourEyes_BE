package com.example.myApp.controller;

import com.example.myApp.dto.CartRequest;
import com.example.myApp.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<String> addToCart(@RequestBody CartRequest cartRequest, Principal principal){
        String email = principal.getName();
        cartService.addToCart(email, cartRequest.getProductId(), cartRequest.getQuantity());
        return ResponseEntity.ok("Add to cart successfully");
    }
}
