package com.example.myApp.controller;

import com.example.myApp.dto.CartRequest;
import com.example.myApp.enity.Cart;
import com.example.myApp.service.CartService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestBody CartRequest cartRequest, Principal principal) {
        try {
            String email = principal.getName();
            cartService.addToCart(email, cartRequest.getProductId(), cartRequest.getQuantity());
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
    public ResponseEntity<?> removeFromCart(@PathVariable int productId, Principal principal) {
        try {
            String email = principal.getName();
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
    public ResponseEntity<?> getCart(Principal principal) {
        try {
            String email = principal.getName();
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
    public ResponseEntity<?> updateCart(@RequestBody CartRequest cartRequest, Principal principal) {
        try {
            String email = principal.getName();
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
