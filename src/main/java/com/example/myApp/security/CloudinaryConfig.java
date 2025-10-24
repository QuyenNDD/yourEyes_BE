package com.example.myApp.security;

import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {
    @Bean
    public Cloudinary cloudinary(CloudinaryConfigProperties configProperties) {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", configProperties.getCloudName());
        config.put("api_key", configProperties.getApiKey());
        config.put("api_secret", configProperties.getApiSecret());
        return new Cloudinary(config);
    }
}
