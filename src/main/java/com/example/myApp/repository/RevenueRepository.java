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
            WHERE YEAR(o.created_at) = :year AND MONTH(o.created_at) = :month AND o.status = 'COMPLETED'
            GROUP BY c.name, MONTH(o.created_at)
            ORDER BY MONTH(o.created_at), c.name;
            """, nativeQuery = true)
    List<Object[]> getCategoryMonthMoney(@Param("year") int year,
                                        @Param("month") int month);


    @Query(value = """
                SELECT
                                    c.name AS category_name,
                                    SUM(od.price * od.quantity) AS total_revenue
                                FROM orders o
                                JOIN order_details od ON o.id = od.order_id
                                JOIN products p ON od.product_id = p.id
                                JOIN categories c ON p.category_id = c.id
                                WHERE o.status = 'COMPLETED' and YEAR(o.created_at) = :year
                                GROUP BY c.name
                                ORDER BY c.name;
            """, nativeQuery = true)
    List<Object[]> getCategoryYearMoney(@Param("year") int year);

    @Query(value = """
                WITH Months AS (
                    SELECT 1 AS month
                    UNION ALL SELECT 2
                    UNION ALL SELECT 3
                    UNION ALL SELECT 4
                    UNION ALL SELECT 5
                    UNION ALL SELECT 6
                    UNION ALL SELECT 7
                    UNION ALL SELECT 8
                    UNION ALL SELECT 9
                    UNION ALL SELECT 10
                    UNION ALL SELECT 11
                    UNION ALL SELECT 12
                )
                SELECT
                    m.month,
                    ISNULL(COUNT(o.id), 0) AS total_orders
                FROM Months m
                LEFT JOIN orders o
                    ON MONTH(o.created_at) = m.month
                    AND YEAR(o.created_at) = :year
                    AND o.status = 'COMPLETED'
                GROUP BY m.month
                ORDER BY m.month;
            """, nativeQuery = true)
    List<Object[]> getMonthlyOrderRevenue(@Param("year") int year);
}
