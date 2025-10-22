package com.example.myApp.service.serviceImpl;

import com.example.myApp.dto.*;
import com.example.myApp.enity.*;
import com.example.myApp.enums.OrderStatus;
import com.example.myApp.repository.*;
import com.example.myApp.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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

    @Override
    public OrderResponse placeOrder(String userEmail, String discountCode, List<Integer> cartItemIds) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User không tồn tại!"));
        if (cartItemIds == null || cartItemIds.isEmpty()) {
            throw new RuntimeException("Chưa chọn sản phẩm nào để đặt hàng!");
        }
        List<Cart> cartItems = cartRepository.findByIdInAndUser(cartItemIds, user);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Không tìm thấy sản phẩm trong giỏ hàng đã chọn!");
        }
        Discount discount = null;
        if (discountCode != null && !discountCode.isEmpty()) {
            discount = discountRepository.findByCode(discountCode)
                    .orElseThrow(() -> new RuntimeException("Mã giảm giá không tồn tại!"));
        }
        BigDecimal totalPrice = cartItems.stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal finalPrice = totalPrice;
        if (discount != null && discount.getDiscountPercentage() != null) {
            BigDecimal discountAmount = totalPrice.multiply(discount.getDiscountPercentage())
                    .divide(BigDecimal.valueOf(100));
            finalPrice = totalPrice.subtract(discountAmount);
        }
        Order order = Order.builder()
                .user(user)
                .discount(discount)
                .status(OrderStatus.PENDING)
                .totalPrice(totalPrice)
                .finalPrice(finalPrice)
                .createdAt(LocalDateTime.now())
                .build();
        Order savedOrder = orderRepository.save(order);

        for (Cart cartItem : cartItems) {
            BigDecimal itemTotalPrice = cartItem.getProduct().getPrice()
                    .multiply(BigDecimal.valueOf(cartItem.getQuantity()));

            OrderDetail orderDetail = OrderDetail.builder()
                    .order(savedOrder)
                    .products(cartItem.getProduct())
                    .quantity(cartItem.getQuantity())
                    .price(cartItem.getProduct().getPrice())
                    .totalPrice(itemTotalPrice)
                    .createdAt(LocalDateTime.now())
                    .build();
            orderDetailRepository.save(orderDetail);

            // Xóa cartItem sau khi đặt
            cartRepository.delete(cartItem);
        }

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
                            orderDetail -> orderDetail.getProducts().getId(),
                            OrderDetail::getQuantity
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
                        detail.getPrice(),
                        detail.getProducts().getImageUrl()
                )
        ).collect(Collectors.toList());
        return new OrderDetailResponse(order.getId(), order.getFinalPrice(), order.getTotalPrice(),order.getStatus(), products);
    }
    @Override
    public Order updateOrderStatus(int id, OrderStatus newStatus, String email){
        User employee = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
//        if (!employee.getRole().equals("EMPLOYEE") && !employee.getRole().equals("ADMIN")) {
//            throw new RuntimeException("Employee does not have permission to update status");
//        }
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        OrderStatus currentStatus = order.getStatus();

        if (!isValidTransition(currentStatus, newStatus)) {
            throw new IllegalStateException("Không thể chuyển từ " + currentStatus + " sang " + newStatus);
        }

        order.setStatus(newStatus);
        return orderRepository.save(order);
    }

    private boolean isValidTransition(OrderStatus current, OrderStatus next) {
        return switch (current) {
            case PENDING -> next == OrderStatus.CONFIRMED || next == OrderStatus.CANCELED;
            case CONFIRMED -> next == OrderStatus.SHIPPING || next == OrderStatus.CANCELED;
            case SHIPPING -> next == OrderStatus.COMPLETED || next == OrderStatus.RETURNED || next == OrderStatus.CANCELED;
            default -> false;
        };
    }

    @Override
    public List<Order> findOrderByStatus(OrderStatus status){
        return orderRepository.findByStatus(status);
    }
}
