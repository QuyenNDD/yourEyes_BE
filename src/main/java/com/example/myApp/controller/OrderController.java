package com.example.myApp.controller;

import com.example.myApp.dto.request.PlaceOrderRequest;
import com.example.myApp.dto.response.OrderDetailResponse;
import com.example.myApp.dto.response.OrderHistoryResponse;
import com.example.myApp.dto.response.OrderResponse;
import com.example.myApp.enity.Order;
import com.example.myApp.enums.OrderStatus;
import com.example.myApp.repository.OrderRepository;
import com.example.myApp.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/order")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderRepository orderRepository;

//    Lay don hang cua mot nguoi
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getOrderByUserId(@PathVariable int userId,
                                              HttpServletRequest request){
        try {
            Integer roleId = (Integer) request.getAttribute("roleId");
            System.out.println(userId);
            if (roleId != 2 || roleId == null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bạn không có quyền");
            }
            List<Order> orders = orderService.findOrderByUserId(userId);
            if (orders.isEmpty()){
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(orders);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", 500,
                    "error", "Lỗi hệ thống",
                    "message", e.getMessage()
            ));
        }
    }
    @PostMapping("/place")
    public ResponseEntity<?> placeOrder(@RequestBody PlaceOrderRequest placeOrderRequest,
                                        HttpServletRequest request) {
        try {
            String userEmail = (String) request.getAttribute("email");
            OrderResponse response = orderService.placeOrder(userEmail, placeOrderRequest.getDiscountCode(), placeOrderRequest.getItems());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }

    }

    @GetMapping("/history")
    public ResponseEntity<List<OrderHistoryResponse>> getOrderHistory(HttpServletRequest request) {
        String userEmail = (String) request.getAttribute("email"); // Lấy email từ token
        List<OrderHistoryResponse> orderHistory = orderService.getOrderHistory(userEmail);
        return ResponseEntity.ok(orderHistory);
    }

    @GetMapping("history/{orderId}")
    public ResponseEntity<?> getOrderHistoryByOrderId(@PathVariable int orderId
                                                    ,HttpServletRequest request){
        try {
            String email = (String) request.getAttribute("email");;
            OrderDetailResponse orderDetailResponse = orderService.getOrderDetail(orderId,email);
            return ResponseEntity.ok(orderDetailResponse);
        }catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }
    @PutMapping("/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable int orderId,
                                                @RequestParam OrderStatus newStatus,
                                                HttpServletRequest request) {
        try {
            Integer roleId = (Integer) request.getAttribute("roleId");
            if (roleId == null || roleId != 2) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bạn không có quyền");
            }
            String email = (String) request.getAttribute("email");
            Order updateOrder = orderService.updateOrderStatus(orderId, newStatus, email);
            return ResponseEntity.ok(updateOrder);
        }catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    @GetMapping
    public ResponseEntity<?> getOrdersByStatus(@RequestParam OrderStatus status,
                                                         HttpServletRequest request) {
        Integer roleId = (Integer) request.getAttribute("roleId");
        if (roleId == null || roleId != 2) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bạn không có quyền");
        }
        List<Order> orders = orderService.findOrderByStatus(status);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllOrders(HttpServletRequest request) {
        Integer roleId = (Integer) request.getAttribute("roleId");
        if (roleId == null || roleId != 2) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bạn không có quyền");
        }
        List<Order> orders = orderRepository.findAll();
        return ResponseEntity.ok(orders);
    }
}
