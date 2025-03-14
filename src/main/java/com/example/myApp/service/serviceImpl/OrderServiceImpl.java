package com.example.myApp.service.serviceImpl;

import com.example.myApp.enity.Order;
import com.example.myApp.repository.OrderRepository;
import com.example.myApp.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Override
    public List<Order> findOrderByUserId(Integer id){
        return orderRepository.findOrderByUserId(id);
    }
}
