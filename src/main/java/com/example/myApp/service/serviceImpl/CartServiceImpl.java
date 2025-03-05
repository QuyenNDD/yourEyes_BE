package com.example.myApp.service.serviceImpl;

import com.example.myApp.enity.Cart;
import com.example.myApp.enity.Products;
import com.example.myApp.enity.User;
import com.example.myApp.repository.CartRepository;
import com.example.myApp.repository.ProductRepository;
import com.example.myApp.repository.UserRepository;
import com.example.myApp.service.CartService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
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

    @Override
    public void removeFromCart(String email, int productId){
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng"));
        Products products = productRepository.findById(productId).orElseThrow(() -> new EntityNotFoundException("Không tìm thấy sản phẩm"));
        Cart cart = cartRepository.findByUserAndProduct(user, products).orElseThrow(() -> new EntityNotFoundException("Không tìm thấy sản phẩm trong giỏ hàng"));
        cartRepository.delete(cart);
    }

    @Override
    public List<Cart> getCartByUser(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng"));
        return cartRepository.findByUser(user);
    }

    @Override
    public void updateCart(String email, int productId, int newQuantity){
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng"));
        Products products = productRepository.findById(productId).orElseThrow(() -> new EntityNotFoundException("Không tìm thấy sản phẩm"));
        Cart cartItems = cartRepository.findByUserAndProduct(user, products).orElseThrow(() -> new EntityNotFoundException("Không tìm thấy sản phẩm trong giỏ hàng"));

        if (newQuantity > 0){
            cartItems.setQuantity(newQuantity);
            cartRepository.save(cartItems);
        }else {
            cartRepository.delete(cartItems);
        }
    }
}
