package com.example.myApp.service;

import com.example.myApp.dto.ForgetPasswordRequest;
import com.example.myApp.dto.UserUpdateRequest;
import com.example.myApp.dto.login.RegisterRequest;
import com.example.myApp.dto.login.UserDTO;
import com.example.myApp.enity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<User> loginEmail(String email, String password);
    void registerUser(RegisterRequest request);
    String authenticate(String email, String password);
    UserDTO getUserProfile(String email);
    void updateUserProfile(String email, UserUpdateRequest userUpdateRequest);
    List<UserDTO> getAllUsers();
    String resetPassword(ForgetPasswordRequest request);
}
