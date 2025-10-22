package com.example.myApp.repository;

import com.example.myApp.enity.Category;
import com.example.myApp.enity.Products;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Products, Integer> {
    @Query(value = "SELECT * FROM products WHERE name COLLATE Latin1_General_CI_AI LIKE CONCAT('%', ?1, '%')",
            countQuery = "SELECT COUNT(*) FROM products WHERE name COLLATE Latin1_General_CI_AI LIKE CONCAT('%', ?1, '%')",
            nativeQuery = true)
    Page<Products> searchProductsByName(String name, Pageable pageable);

    Optional<Products> findByName(String name);

    @Query("SELECT p FROM Products p WHERE " +
            "(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
            "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.price <= :maxPrice)")
    List<Products> findByFilters(@Param("categoryId") Integer categoryId,
                                 @Param("minPrice") BigDecimal minPrice,
                                 @Param("maxPrice") BigDecimal maxPrice);
}
