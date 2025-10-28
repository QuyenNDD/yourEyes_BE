package com.example.myApp.repository;

import com.example.myApp.dto.response.CategoryMonthMoney;
import com.example.myApp.enity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RevenueRepository extends JpaRepository<Order, Integer> {
    @Query(value = """
            SELECT
                c.name AS category_name,
                MONTH(o.created_at) AS month,
                SUM(od.price * od.quantity) AS total_revenue
            FROM orders o
            JOIN order_details od ON o.id = od.order_id
            JOIN products p ON od.product_id = p.id
            JOIN categories c ON p.category_id = c.id
            WHERE YEAR(o.created_at) = :year AND MONTH(o.created_at) = :month
            GROUP BY c.name, MONTH(o.created_at)
            ORDER BY MONTH(o.created_at), c.name;
            """, nativeQuery = true)
    List<Object[]> getCategoryMonthMoney(@Param("year") int year,
                                        @Param("month") int month);

}
