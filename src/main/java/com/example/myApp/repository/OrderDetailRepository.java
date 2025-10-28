package com.example.myApp.repository;

import com.example.myApp.enity.Order;
import com.example.myApp.enity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {
    List<OrderDetail> findByOrder(Order order);

    @Query("""
        SELECT COUNT(od)
        FROM OrderDetail od
        WHERE od.order.user.id = :userId
          AND od.products.id = :productId
          AND od.order.status = 'COMPLETED'
    """)
    int countPurchasedProduct(@Param("userId") Integer userId,
                              @Param("productId") Integer productId);
}
