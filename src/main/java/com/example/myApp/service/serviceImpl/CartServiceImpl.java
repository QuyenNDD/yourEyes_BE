package com.example.myApp.service.serviceImpl;

import com.example.myApp.enity.Cart;
import com.example.myApp.enity.Products;
import com.example.myApp.enity.User;
import com.example.myApp.repository.CartRepository;
import com.example.myApp.repository.ProductRepository;
import com.example.myApp.repository.UserRepository;
import com.example.myApp.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    public void addToCart(String email, int productId, int quantity){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Products products = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        Optional<Cart> existingCart = cartRepository.findByUserAndProduct(user, products);
        if (existingCart.isPresent()){
            Cart cart = existingCart.get();
            cart.setQuantity(cart.getQuantity() + quantity);
            cartRepository.save(cart);
        }else {
            Cart newCart = Cart.builder()
                    .user(user)
                    .product(products)
                    .quantity(quantity)
                    .createdAt(LocalDateTime.now())
                    .build();
            cartRepository.save(newCart);
        }
    }
}
