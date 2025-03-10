package com.example.myApp.enity;

import com.example.myApp.dto.ProductDTO;
import com.example.myApp.repository.CategoryRepository;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "products")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Products {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private int id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false,  precision = 10, scale = 2)
    private BigDecimal price;

    private int stock;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category categoryId;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public void updateFromDTO(ProductDTO productDTO,
                              CategoryRepository categoryRepository) {
        this.name = productDTO.getName();
        this.description = productDTO.getDescription();
        this.price = productDTO.getPrice();
        this.imageUrl = productDTO.getImageUrl();

        // Tìm category theo tên và cập nhật
        this.categoryId = categoryRepository.findByName(productDTO.getCategory())
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }
}
