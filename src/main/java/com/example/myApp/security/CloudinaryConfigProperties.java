package com.example.myApp.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "cloudinary")
@Data
public class CloudinaryConfigProperties {
    private String cloudName;
    private String apiKey;
    private String apiSecret;
}
