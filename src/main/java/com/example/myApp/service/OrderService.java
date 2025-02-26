package com.example.myApp.service;


import com.example.myApp.enity.Order;

import java.util.List;

public interface OrderService {
    List<Order> findOrderByUserId (Integer userId);
}
