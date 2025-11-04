package com.example.myApp.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileRequest {
    public String gender;
    public int age;
    public BigDecimal height;
    public BigDecimal weight;
    public String style_preference;
}
