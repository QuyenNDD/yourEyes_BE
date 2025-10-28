package com.example.myApp.controller;

import com.example.myApp.dto.request.CartRequest;
import com.example.myApp.enity.Cart;
import com.example.myApp.service.CartService;
import com.example.myApp.service.UserProductActivityService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final UserProductActivityService userProductActivityService;

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestBody CartRequest cartRequest,
                                       HttpServletRequest request) {
        try {
            String email = (String) request.getAttribute("email");
            Integer userId = (Integer) request.getAttribute("userId");
            if (email == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Vui lòng đăng nhập"));
            }
            cartService.addToCart(email, cartRequest.getProductId(), cartRequest.getQuantity());
            userProductActivityService.logActivity(userId, cartRequest.getProductId(), "ADD_TO_CART");
            return ResponseEntity.ok(Map.of("message", "Thêm vào giỏ hàng thành công"));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", 404,
                    "error", "Không tìm thấy dữ liệu",
                    "message", ex.getMessage()
            ));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", 500,
                    "error", "Lỗi hệ thống",
                    "message", ex.getMessage()
            ));
        }
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<?> removeFromCart(@PathVariable int productId,
                                            HttpServletRequest request) {
        try {
            String email = (String) request.getAttribute("email");
            cartService.removeFromCart(email, productId);
            return ResponseEntity.ok(Map.of("message", "Xóa sản phẩm khỏi giỏ hàng thành công"));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", 404,
                    "error", "Không tìm thấy sản phẩm trong giỏ hàng",
                    "message", ex.getMessage()
            ));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", 500,
                    "error", "Lỗi hệ thống",
                    "message", ex.getMessage()
            ));
        }
    }

    @GetMapping
    public ResponseEntity<?> getCart(HttpServletRequest request) {
        try {
            String email = (String) request.getAttribute("email");
            List<Cart> cartItems = cartService.getCartByUser(email);
            return ResponseEntity.ok(Map.of("cart", cartItems));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", 500,
                    "error", "Lỗi hệ thống",
                    "message", ex.getMessage()
            ));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateCart(@RequestBody CartRequest cartRequest,
                                        HttpServletRequest request) {
        try {
            String email = (String) request.getAttribute("email");
            cartService.updateCart(email, cartRequest.getProductId(), cartRequest.getQuantity());
            return ResponseEntity.ok(Map.of("message", "Cập nhật giỏ hàng thành công"));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", 404,
                    "error", "Không tìm thấy sản phẩm trong giỏ hàng",
                    "message", ex.getMessage()
            ));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", 500,
                    "error", "Lỗi hệ thống",
                    "message", ex.getMessage()
            ));
        }
    }
}
