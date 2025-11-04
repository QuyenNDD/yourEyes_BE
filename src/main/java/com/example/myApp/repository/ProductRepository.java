package com.example.myApp.repository;

import com.example.myApp.enity.Category;
import com.example.myApp.enity.Products;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Products, Integer> {
    @Query(value = "SELECT * FROM Products p WHERE p.name COLLATE Latin1_General_CI_AI LIKE CONCAT('%', ?1, '%')",
            countQuery = "SELECT COUNT(*) FROM Products WHERE p.name COLLATE Latin1_General_CI_AI LIKE CONCAT('%', ?1, '%')",
            nativeQuery = true)
    Page<Products> searchProductsByName(String name, Pageable pageable);

    @Query("SELECT p FROM Products p WHERE p.isActive = true")
    Page<Products> findAllActive(Pageable pageable);

    Optional<Products> findByName(String name);

    @Query("SELECT p FROM Products p WHERE " +
            "(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
            "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
            "(:color IS NULL OR p.color = :color) AND " +
            "(:genderTarget IS NULL OR p.genderTarget = :genderTarget)")
    List<Products> findByFilters(@Param("categoryId") Integer categoryId,
                                 @Param("minPrice") BigDecimal minPrice,
                                 @Param("maxPrice") BigDecimal maxPrice,
                                 @Param("color") String color,
                                 @Param("genderTarget") String genderTarget);

    @Modifying
    @Transactional
    @Query("UPDATE Products p SET p.isActive = false WHERE p.id = :id")
    void deleteProduct(@Param("id") int id);

    @Modifying
    @Transactional
    @Query("UPDATE Products p SET p.isActive = true WHERE p.id = :id")
    void restoreProduct(@Param("id") int id);

    @Query("""
            SELECT r.products
                    FROM Recommendation r
                    WHERE r.user.id = :userId AND r.products.isActive = true
                    ORDER BY r.score DESC
            """)
    List<Products> recommendationProducts(@Param("userId") Integer userId);

    @Query("""
            SELECT p
                    FROM UserProductActivity upa
                    JOIN upa.products p
                    JOIN upa.user u
                    JOIN UserProfile up ON up.user.id = u.id
                    WHERE up.clusterId = :clusterId AND upa.actionType = 'ORDER' AND p.isActive = true
                    GROUP BY p
                    ORDER BY COUNT(upa.id) DESC
            """)
    List<Products> findBestSellersForCluster(@Param("clusterId") Integer clusterId, Pageable pageable);
}
