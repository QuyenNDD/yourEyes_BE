package com.example.myApp.service;

import com.example.myApp.dto.ReviewRequest;
import com.example.myApp.dto.ReviewResponse;
import com.example.myApp.enity.Review;

import java.util.List;

public interface ReviewService {
    Review addReview(String email, ReviewRequest reviewRequest);
    List<ReviewResponse> getReviewsByProduct(int productId);
}
