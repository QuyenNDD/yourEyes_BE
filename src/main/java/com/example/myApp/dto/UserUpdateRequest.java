package com.example.myApp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserUpdateRequest {
    @NotBlank(message = "Họ và tên không được để trống")
    private String fullname;

    @NotBlank(message = "Số điện thoại không được để trống")
    private String phone;

    @NotBlank(message = "Địa chỉ không được để trống")
    private String address;
}
