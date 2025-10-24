package com.example.myApp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    private String userFullName;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;
}
