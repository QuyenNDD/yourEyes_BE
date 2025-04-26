package com.example.myApp.service.serviceImpl;

import com.example.myApp.dto.OrderDetailResponse;
import com.example.myApp.dto.OrderHistoryResponse;
import com.example.myApp.dto.OrderProductResponse;
import com.example.myApp.dto.OrderResponse;
import com.example.myApp.enity.*;
import com.example.myApp.enums.OrderStatus;
import com.example.myApp.repository.*;
import com.example.myApp.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private DiscountRepository discountRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Override
    public List<Order> findOrderByUserId(Integer id){
        return orderRepository.findOrderByUserId(id);
    }

    @Transactional
    public OrderResponse placeOrder(String userEmail, String discountCode) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User không tồn tại!"));

        List<Cart> cartItems = cartRepository.findByUser(user);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Giỏ hàng trống!");
        }

        Discount discount = null;
        if (discountCode != null && !discountCode.isEmpty()) {
            discount = discountRepository.findByCode(discountCode)
                    .orElseThrow(() -> new RuntimeException("Mã giảm giá không tồn tại!"));
        }

        // Chỉ lưu order mà không tính toán giá tiền
        Order order = Order.builder()
                .user(user)
                .discount(discount)
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        Order savedOrder = orderRepository.save(order);

        return new OrderResponse(savedOrder);
    }

    @Override
    public List<OrderHistoryResponse> getOrderHistory(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User không tồn tại!"));
        List<Order> orders = orderRepository.findByUserOrderByCreatedAtDesc(user);

        return orders.stream().map(order -> {
            Map<Integer, Integer> products = orderDetailRepository.findByOrder(order)
                    .stream()
                    .collect(Collectors.toMap(
                            orderDetail -> orderDetail.getProducts().getId(), // 🛑 Lấy productId
                            OrderDetail::getQuantity         // 🛑 Lấy số lượng
                    ));

            return new OrderHistoryResponse(order.getId(), order.getStatus(), order.getCreatedAt(),
                    order.getFinalPrice(), products);
        }).collect(Collectors.toList());
    }

    @Override
    public OrderDetailResponse getOrderDetail(int orderId, String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User không tồn tại!"));


        Order order = orderRepository.findById(orderId)
           .orElseThrow(() -> new RuntimeException("Order không tồn tại!"));

        if (!order.getUser().equals(user)) {
            throw new RuntimeException("Bạn không có quyền xem đơn hàng này!");
        }

        List<OrderDetail> orderDetails = orderDetailRepository.findByOrder(order);
        List<OrderProductResponse> products = orderDetails.stream().map(detail ->
                new OrderProductResponse(
                        detail.getProducts().getId(),
                        detail.getProducts().getName(),
                        detail.getQuantity(),
                        detail.getPrice()
                )
        ).collect(Collectors.toList());
        return new OrderDetailResponse(order.getId(), order.getFinalPrice(), order.getTotalPrice(),order.getStatus(), products);
    }
    @Override
    public Order updateOrderStatus(int id, OrderStatus newStatus, String email){
        User employee = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        if (!employee.getRole().getName().equals("EMPLOYEE") && !employee.getRole().getName().equals("ADMIN")) {
            throw new RuntimeException("Employee does not have permission to update status");
        }
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(newStatus);
        return orderRepository.save(order);
    }

    @Override
    public List<Order> findOrderByStatus(OrderStatus status){
        return orderRepository.findByStatus(status);
    }
}
