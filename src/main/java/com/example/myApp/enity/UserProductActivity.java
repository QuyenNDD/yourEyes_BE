package com.example.myApp.enity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "User_Product_Activity")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProductActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Products products;

    @Column(name = "action_type")
    private String actionType;

    @Column(name = "action_time")
    private LocalDateTime actionTime = LocalDateTime.now();
}
