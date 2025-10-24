package com.example.myApp.service.serviceImpl;

import com.example.myApp.dto.request.ReviewRequest;
import com.example.myApp.dto.response.ReviewResponse;
import com.example.myApp.enity.Products;
import com.example.myApp.enity.Review;
import com.example.myApp.enity.User;
import com.example.myApp.repository.OrderDetailRepository;
import com.example.myApp.repository.ProductRepository;
import com.example.myApp.repository.ReviewRepository;
import com.example.myApp.repository.UserRepository;
import com.example.myApp.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderDetailRepository orderDetailRepository;

    @Override
    public Review addReview(String email, ReviewRequest reviewRequest) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Products products = productRepository.findById(reviewRequest.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        int count = orderDetailRepository.countPurchasedProduct(user.getId(), products.getId());
        if (count == 0) {
            throw new RuntimeException("Bạn chỉ có thể đánh giá sản phẩm khi đã mua hàng.");
        }
        if (reviewRequest.getRating() < 1 || reviewRequest.getRating() > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }
        Review review = Review.builder()
                .user(user)
                .product(products)
                .rating(reviewRequest.getRating())
                .comment(reviewRequest.getComment())
                .createdAt(LocalDateTime.now())
                .build();

        return reviewRepository.save(review);
    }

    @Override
    public List<ReviewResponse> getReviewsByProduct(int productId) {
        List<Review> reviews = reviewRepository.findByProductId(productId);

        return reviews.stream().map(review -> new ReviewResponse(
                review.getUser().getFullname(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt()
        )).collect(Collectors.toList());
    }
}
