package com.example.myApp.service.serviceImpl;

import com.example.myApp.dto.OrderResponse;
import com.example.myApp.enity.Cart;
import com.example.myApp.enity.Discount;
import com.example.myApp.enity.Order;
import com.example.myApp.enity.User;
import com.example.myApp.repository.CartRepository;
import com.example.myApp.repository.DiscountRepository;
import com.example.myApp.repository.OrderRepository;
import com.example.myApp.repository.UserRepository;
import com.example.myApp.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
                .status("pending")
                .createdAt(LocalDateTime.now())
                .build();

        Order savedOrder = orderRepository.save(order);

        return new OrderResponse(savedOrder);
    }
}
