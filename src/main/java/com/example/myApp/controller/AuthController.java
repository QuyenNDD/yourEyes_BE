package com.example.myApp.controller;

import com.example.myApp.dto.login.AuthResponse;
import com.example.myApp.dto.login.LoginRequest;
import com.example.myApp.dto.login.RegisterRequest;
import com.example.myApp.dto.login.UserDTO;
import com.example.myApp.enity.User;
import com.example.myApp.security.JwtTokenProvider;
import com.example.myApp.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

//    Dang nhap tai khoan
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, BindingResult result) {
        try {
            if (result.hasErrors()){
                return ResponseEntity.badRequest().body(result.getFieldError().getDefaultMessage());
            }
            boolean isAuthenticated = userService.authenticate(request.getEmail(), request.getPassword());
            if (isAuthenticated) {
                String token = jwtTokenProvider.generateToken(request.getEmail());
                return ResponseEntity.ok(new AuthResponse(token));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sai tài khoản hoặc mật khẩu");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
//    Dang kí tai khoan
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody RegisterRequest request, BindingResult result){
        try{
            if (result.hasErrors()){
                return ResponseEntity.badRequest().body(result.getFieldError().getDefaultMessage());
            }
            userService.registerUser(request);
            return ResponseEntity.ok("Register successfully");
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
