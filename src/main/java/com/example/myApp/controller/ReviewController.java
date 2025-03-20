package com.example.myApp.controller;

import com.example.myApp.dto.ReviewRequest;
import com.example.myApp.dto.ReviewResponse;
import com.example.myApp.enity.Review;
import com.example.myApp.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    @Autowired
    private ReviewService reviewService;

    @PostMapping("/add")
    public ResponseEntity<Review> addReview(@RequestBody ReviewRequest reviewRequest, Principal principal){
        String email = principal.getName();
        return ResponseEntity.ok(reviewService.addReview(email, reviewRequest));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByProduct(@PathVariable int productId){
        return ResponseEntity.ok(reviewService.getReviewsByProduct(productId));
    }
}
