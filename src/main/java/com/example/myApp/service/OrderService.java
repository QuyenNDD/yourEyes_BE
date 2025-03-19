package com.example.myApp.service;


import com.example.myApp.dto.OrderDetailResponse;
import com.example.myApp.dto.OrderHistoryResponse;
import com.example.myApp.dto.OrderResponse;
import com.example.myApp.enity.Order;
import com.example.myApp.enity.OrderDetail;
import com.example.myApp.enums.OrderStatus;

import java.util.List;

public interface OrderService {
    List<Order> findOrderByUserId (Integer userId);
    OrderResponse placeOrder(String userEmail, String discountCode);
    List<OrderHistoryResponse> getOrderHistory(String userEmail);
    OrderDetailResponse getOrderDetail(int orderId, String email);
    Order updateOrderStatus(int id, OrderStatus newStatus, String email);
    List<Order> findOrderByStatus(OrderStatus status);
}
