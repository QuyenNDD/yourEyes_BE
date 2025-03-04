package com.example.myApp.dto.login;

import com.example.myApp.enity.Role;
import lombok.Data;

@Data
public class RegisterRequest {
    private String fullname;
    private String email;
    private String password;
    private String phone;
    private String address;
    private Role role;
}
