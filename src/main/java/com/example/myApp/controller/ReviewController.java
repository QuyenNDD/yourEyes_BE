package com.example.myApp.controller;

import com.example.myApp.dto.request.ReviewRequest;
import com.example.myApp.dto.response.ReviewResponse;
import com.example.myApp.service.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    @Autowired
    private ReviewService reviewService;

    @PostMapping("/add")
    public ResponseEntity<?> addReview(@RequestBody ReviewRequest reviewRequest, HttpServletRequest request){
        try {
            String email = (String) request.getAttribute("email");
            return ResponseEntity.ok(reviewService.addReview(email, reviewRequest));
        }catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByProduct(@PathVariable int productId){
        return ResponseEntity.ok(reviewService.getReviewsByProduct(productId));
    }
}
