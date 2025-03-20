package com.example.myApp.repository;

import com.example.myApp.enity.Revenues;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RevenueRepository extends JpaRepository<Revenues, Integer> {
    List<Revenues> findByRevenueYear(Integer year);
    List<Revenues> findByRevenueMonthAndRevenueYear(Integer month, Integer year);
}
