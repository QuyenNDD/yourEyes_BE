package com.example.myApp.repository;

import com.example.myApp.enity.Inventory;
import com.example.myApp.enity.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Integer> {
    Optional<Inventory> findByProducts(Products products);
    List<Inventory> findAllByOrderByStockQuantityAsc();
}
