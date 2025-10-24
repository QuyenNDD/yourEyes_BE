package com.example.myApp.controller;

import com.example.myApp.dto.DiscountDTO;
import com.example.myApp.dto.request.DiscountRequest;
import com.example.myApp.service.DiscountService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/discount")
public class DiscountController {
    @Autowired
    private DiscountService discountService;

    @GetMapping("/available")
    public ResponseEntity<List<DiscountDTO>> getAvailableDiscounts(){
        List<DiscountDTO> discounts = discountService.getAvailableDiscounts();
        return ResponseEntity.ok(discounts);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addDiscount(@RequestBody DiscountRequest discountRequest,
                                         HttpServletRequest request){
        try{
            String email = (String) request.getAttribute("email");
            Integer roleId = (Integer) request.getAttribute("roleId");
            if (email == null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vui lòng đăng nhập");
            }else if (roleId != 2 || roleId == null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bạn không có quyền");
            }
            discountService.addDiscount(discountRequest);
            return ResponseEntity.ok("Discount added successfully");
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
