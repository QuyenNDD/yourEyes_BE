package com.example.myApp.repository;

import com.example.myApp.enity.Order;
import com.example.myApp.enity.User;
import com.example.myApp.enums.OrderStatus;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findOrderByUserId(int id);
    List<Order> findByUserOrderByCreatedAtDesc(User user);
    Optional<Order> findById(int id);
    List<Order> findByStatus(OrderStatus status);
}
