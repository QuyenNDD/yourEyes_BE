package com.example.myApp.repository;

import com.example.myApp.enity.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Integer> {
    boolean existsByCode(String code);
    List<Discount> findByStartDateBeforeAndEndDateAfter(LocalDate startDate, LocalDate endDate);
    Optional<Discount> findByCode(String code);
}
