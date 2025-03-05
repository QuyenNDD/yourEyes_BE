package com.example.myApp.dto.login;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "Email không được để trống")
    private String email;
    private String password;
}
