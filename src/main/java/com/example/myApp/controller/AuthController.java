package com.example.myApp.controller;

import com.example.myApp.enity.User;
import com.example.myApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String fullname, @RequestParam String password){
        User user = userService.findByFullname(fullname).orElse(null);
        if (user != null && password.equals(user.getPassword())){
            return ResponseEntity.ok("Đăng nhập thành công");
        }
        return ResponseEntity.status(401).body("Sai tài khoản hoặc mật khẩu");
    }
}
