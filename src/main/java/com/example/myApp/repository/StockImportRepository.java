package com.example.myApp.repository;

import com.example.myApp.enity.StockImport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockImportRepository extends JpaRepository<StockImport, Integer> {
}
