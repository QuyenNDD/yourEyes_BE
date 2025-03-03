package com.example.myApp.repository;

import com.example.myApp.enity.Cart;
import com.example.myApp.enity.Products;
import com.example.myApp.enity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {
    Optional<Cart> findByUserAndProduct (User user, Products product);
    Optional<Cart> findByUser(User user);
}
