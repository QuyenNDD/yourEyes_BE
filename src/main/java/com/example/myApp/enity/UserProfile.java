package com.example.myApp.enity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user_profile")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile {
    @Id
    private Integer userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String gender;
    private int age;

    @Column(nullable = false,  precision = 5, scale = 2)
    private BigDecimal height;
    @Column(nullable = false,  precision = 5, scale = 2)
    private BigDecimal weight;

    @Column(name = "style_preference")
    private String stylePreference;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    @Column(name = "cluster_id")
    private Integer clusterId;
}
