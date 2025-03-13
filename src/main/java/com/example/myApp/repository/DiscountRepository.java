package com.example.myApp.repository;

import com.example.myApp.enity.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Integer> {
    boolean existsByCode(String code);
}
