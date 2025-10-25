package com.example.myApp.service;


import com.example.myApp.dto.request.OrderItemRequest;
import com.example.myApp.dto.response.OrderDetailResponse;
import com.example.myApp.dto.response.OrderHistoryResponse;
import com.example.myApp.dto.response.OrderResponse;
import com.example.myApp.enity.Order;
import com.example.myApp.enums.OrderStatus;

import java.util.List;

public interface OrderService {
    List<Order> findOrderByUserId (Integer userId);
    OrderResponse placeOrder(String userEmail, String discountCode, List<OrderItemRequest> cartItemIds);
    List<OrderHistoryResponse> getOrderHistory(String userEmail);
    OrderDetailResponse getOrderDetail(int orderId, String email);
    Order updateOrderStatus(int id, OrderStatus newStatus, String email);
    List<Order> findOrderByStatus(OrderStatus status);
}
