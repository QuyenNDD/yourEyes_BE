package com.example.myApp.controller;

import com.example.myApp.dto.OrderDetailResponse;
import com.example.myApp.dto.OrderHistoryResponse;
import com.example.myApp.dto.OrderResponse;
import com.example.myApp.enity.Order;
import com.example.myApp.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

//    Lay don hang cua mot nguoi
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getOrderByUserId(@PathVariable int userId){
        List<Order> orders = orderService.findOrderByUserId(userId);
        if (orders.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(orders);
    }

    @PostMapping("/place")
    public ResponseEntity<?> placeOrder(@RequestParam(required = false) String discountCode,
                                        Principal principal) {
        String userEmail = principal.getName(); // Lấy email từ token
        OrderResponse response = orderService.placeOrder(userEmail, discountCode);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<List<OrderHistoryResponse>> getOrderHistory(Principal principal) {
        String userEmail = principal.getName(); // Lấy email từ token
        List<OrderHistoryResponse> orderHistory = orderService.getOrderHistory(userEmail);
        return ResponseEntity.ok(orderHistory);
    }

    @GetMapping("history/{orderId}")
    public ResponseEntity<OrderDetailResponse> getOrderHistoryByOrderId(@PathVariable int orderId
                                                    ,Principal principal){
        String email = principal.getName();
        OrderDetailResponse orderDetailResponse = orderService.getOrderDetail(orderId,email);
        return ResponseEntity.ok(orderDetailResponse);
    }
}
