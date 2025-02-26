package com.example.myApp.service;

import com.example.myApp.enity.User;

import java.util.Optional;

public interface UserService {
    Optional<User> findByFullname(String fullname);
}
