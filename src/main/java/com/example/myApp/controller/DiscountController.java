package com.example.myApp.controller;

import com.example.myApp.dto.DiscountRequest;
import com.example.myApp.enity.Discount;
import com.example.myApp.service.DiscountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/discount")
public class DiscountController {
    @Autowired
    private DiscountService discountService;

    @GetMapping("/getAll")
    public ResponseEntity<List<Discount>> getAll(){
        return ResponseEntity.ok(discountService.getAllDiscount());
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addDiscount(@RequestBody DiscountRequest discountRequest){
        try{
            discountService.addDiscount(discountRequest);
            return ResponseEntity.ok("Discount added successfully");
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
