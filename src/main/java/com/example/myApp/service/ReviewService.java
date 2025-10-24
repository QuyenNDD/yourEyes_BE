package com.example.myApp.service;

import com.example.myApp.dto.request.ReviewRequest;
import com.example.myApp.dto.response.ReviewResponse;
import com.example.myApp.enity.Review;

import java.util.List;

public interface ReviewService {
    Review addReview(String email, ReviewRequest reviewRequest);
    List<ReviewResponse> getReviewsByProduct(int productId);
}
