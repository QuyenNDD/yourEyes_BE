package com.example.myApp.controller;

import com.example.myApp.dto.PlaceOrderRequest;
import com.example.myApp.dto.OrderDetailResponse;
import com.example.myApp.dto.OrderHistoryResponse;
import com.example.myApp.dto.OrderResponse;
import com.example.myApp.enity.Order;
import com.example.myApp.enums.OrderStatus;
import com.example.myApp.repository.OrderRepository;
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
    @Autowired
    private OrderRepository orderRepository;

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
    public ResponseEntity<?> placeOrder(@RequestBody PlaceOrderRequest placeOrderRequest,
                                        Principal principal) {
        String userEmail = principal.getName();
        OrderResponse response = orderService.placeOrder(userEmail, placeOrderRequest.getDiscountCode(), placeOrderRequest.getCartItemIds());
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
    @PutMapping("/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable int orderId,
                                                @RequestParam OrderStatus newStatus,
                                                Principal principal) {
        try {
            Order updateOrder = orderService.updateOrderStatus(orderId, newStatus, principal.getName());
            return ResponseEntity.ok(updateOrder);
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping
    public ResponseEntity<List<Order>> getOrdersByStatus(@RequestParam OrderStatus status) {
        List<Order> orders = orderService.findOrderByStatus(status);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return ResponseEntity.ok(orders);
    }
}
