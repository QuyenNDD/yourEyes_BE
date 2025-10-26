package com.example.myApp.dto.request;

import com.example.myApp.dto.ProductDTO;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ProductUpdateRequest {
    private ProductDTO productDTO;
    private String imageUrl ;
    private MultipartFile[] newImages;
}
