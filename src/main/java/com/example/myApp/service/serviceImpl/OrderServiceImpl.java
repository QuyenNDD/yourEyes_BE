package com.example.myApp.service.serviceImpl;

import com.example.myApp.dto.request.OrderItemRequest;
import com.example.myApp.dto.response.OrderDetailResponse;
import com.example.myApp.dto.response.OrderHistoryResponse;
import com.example.myApp.dto.response.OrderProductResponse;
import com.example.myApp.dto.response.OrderResponse;
import com.example.myApp.enity.*;
import com.example.myApp.enums.OrderStatus;
import com.example.myApp.repository.*;
import com.example.myApp.service.OrderService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
    @Autowired
    private UserProductActivityRepository userProductActivityRepository;


    @Override
    public List<Order> findOrderByUserId(Integer id){
        return orderRepository.findOrderByUserId(id);
    }

    @Override
    @Transactional
    public OrderResponse placeOrder(String userEmail, String discountCode, List<OrderItemRequest> items) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User không tồn tại!"));

        if (items == null || items.isEmpty()) {
            throw new RuntimeException("Chưa chọn sản phẩm nào để đặt hàng!");
        }

        List<Integer> cartItemIds = items.stream()
                .map(OrderItemRequest::getCartItemId)
                .toList();

        List<Cart> cartItems = cartRepository.findByIdInAndUser(cartItemIds, user);

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Không tìm thấy sản phẩm trong giỏ hàng đã chọn!");
        }

        Discount discount = null;
        if (discountCode != null && !discountCode.isEmpty()) {
            discount = discountRepository.findByCode(discountCode)
                    .orElseThrow(() -> new RuntimeException("Mã giảm giá không tồn tại!"));
        }

        BigDecimal totalPrice = BigDecimal.ZERO;

        Order order = Order.builder()
                .user(user)
                .discount(discount)
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
        Order savedOrder = orderRepository.save(order);

        for (OrderItemRequest itemReq : items) {
            Cart cartItem = cartItems.stream()
                    .filter(c -> c.getId().equals(itemReq.getCartItemId()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại trong giỏ!"));

            Products product = cartItem.getProduct();

            Integer quantityToOrder = itemReq.getQuantity();
            if (quantityToOrder <= 0) {
                throw new RuntimeException("Số lượng phải lớn hơn 0!");
            }

            // ✅ Kiểm tra số lượng tồn kho
            if (product.getStock() < quantityToOrder) {
                throw new RuntimeException("Sản phẩm " + product.getName() + " không đủ số lượng tồn!");
            }

            BigDecimal itemTotalPrice = product.getPrice()
                    .multiply(BigDecimal.valueOf(quantityToOrder));

            totalPrice = totalPrice.add(itemTotalPrice);

            OrderDetail orderDetail = OrderDetail.builder()
                    .order(savedOrder)
                    .products(product)
                    .quantity(quantityToOrder)
                    .price(product.getPrice())
                    .totalPrice(itemTotalPrice)
                    .createdAt(LocalDateTime.now())
                    .build();
            orderDetailRepository.save(orderDetail);

            UserProductActivity userProductActivity = UserProductActivity.builder()
                    .user(user)
                    .products(product)
                    .actionType("ORDER")
                    .actionTime(LocalDateTime.now())
                    .build();
            userProductActivityRepository.save(userProductActivity);
            cartRepository.delete(cartItem);
        }

        BigDecimal finalPrice = totalPrice;

        if (discount != null && discount.getDiscountPercentage() != null) {
            BigDecimal discountAmount = totalPrice.multiply(discount.getDiscountPercentage())
                    .divide(BigDecimal.valueOf(100));
            finalPrice = totalPrice.subtract(discountAmount);
        }

        savedOrder.setTotalPrice(totalPrice);
        savedOrder.setFinalPrice(finalPrice);
        orderRepository.save(savedOrder);

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

        if (user.getRole() != 2){
            if (!order.getUser().equals(user)) {
                throw new RuntimeException("Bạn không có quyền xem đơn hàng này!");
            }
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
