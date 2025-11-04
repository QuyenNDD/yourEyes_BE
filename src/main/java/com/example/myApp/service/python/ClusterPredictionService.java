package com.example.myApp.service.python;

import com.example.myApp.dto.request.UserProfileRequest;
import com.example.myApp.dto.response.ClusterResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class ClusterPredictionService {
    private final RestTemplate restTemplate;

    // Lấy URL của API Python từ file application.properties
    @Value("${ai.api.url.cluster}")
    private String clusterApiUrl; // Ví dụ: http://localhost:5001/predict_cluster

    public ClusterPredictionService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Gọi API Python (FastAPI) để dự đoán cụm cho người dùng mới.
     * @param profile Dữ liệu hồ sơ người dùng mới
     * @return ID của Cụm (Cluster ID), hoặc Optional.empty() nếu thất bại.
     */
    public Optional<Integer> predictCluster(UserProfileRequest profile) {

        System.out.println("Đang gửi request đến AI API cho user " + profile.age + " tuổi...");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<UserProfileRequest> requestEntity = new HttpEntity<>(profile, headers);

        try {
            // Gửi POST request và nhận kết quả
            ResponseEntity<ClusterResponse> response = restTemplate.postForEntity(
                    clusterApiUrl,
                    requestEntity,
                    ClusterResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Integer clusterId = response.getBody().cluster_id;
                System.out.println("Nhận được kết quả: Cụm " + clusterId);
                return Optional.of(clusterId);
            } else {
                System.err.println("Lỗi khi gọi AI API: " + response.getStatusCode());
                return Optional.empty();
            }

        } catch (HttpClientErrorException e) {
            // Lỗi 4xx (ví dụ: Python API trả về 404 hoặc 422)
            System.err.println("Lỗi Client khi gọi AI API: " + e.getMessage());
            return Optional.empty();
        } catch (Exception e) {
            // Lỗi 5xx (ví dụ: API Python bị sập)
            System.err.println("Lỗi Server khi gọi AI API: " + e.getMessage());
            return Optional.empty();
        }
    }
}
