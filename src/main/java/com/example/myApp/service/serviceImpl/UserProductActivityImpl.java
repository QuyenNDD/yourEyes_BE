package com.example.myApp.service.serviceImpl;

import com.example.myApp.enity.UserProductActivity;
import com.example.myApp.repository.ProductRepository;
import com.example.myApp.repository.UserProductActivityRepository;
import com.example.myApp.repository.UserRepository;
import com.example.myApp.service.UserProductActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserProductActivityImpl implements UserProductActivityService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final UserProductActivityRepository userProductActivityRepository;

    @Override
    public void logActivity(Integer userId, Integer productId, String activityType) {
        if (userId == null) return;;

        userRepository.findById(userId).ifPresent(user -> {
            productRepository.findById(productId).ifPresent(product -> {
                UserProductActivity userProductActivity = UserProductActivity.builder()
                        .user(user)
                        .products(product)
                        .actionType(activityType)
                        .actionTime(LocalDateTime.now())
                        .build();
                userProductActivityRepository.save(userProductActivity);
            });
        });
    }
}
