package com.example.myApp.dto.login;

import com.example.myApp.enity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Họ và tên không được để trống")
    private String fullname;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    @NotBlank(message = "Password không được để trống")
    @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 kí tự")
    private String password;

    @NotBlank(message = "Số điện thoại không được để trống")
    private String phone;

    @NotBlank(message = "Địa chỉ không được để trống")
    private String address;
}
