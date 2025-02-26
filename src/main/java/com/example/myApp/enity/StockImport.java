package com.example.myApp.enity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "stock_imports")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockImport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Products products;

    @Column(name = "import_quantity", nullable = false)
    private int importQuantity;

    @Column(name = "import_date")
    private LocalDateTime importDate = LocalDateTime.now();

    @Column(name = "code_id", nullable = false)
    private String codeId;

    @ManyToOne
    @JoinColumn(name = "id_employee", nullable = false)
    private User user;
}
