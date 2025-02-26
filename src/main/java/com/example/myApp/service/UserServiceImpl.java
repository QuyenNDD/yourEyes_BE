package com.example.myApp.service;

import com.example.myApp.enity.User;
import com.example.myApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{
    @Autowired
    private UserRepository userRepository;

    @Override
    public Optional<User> findByFullname(String fullname){
        return userRepository.findByFullname(fullname);
    }

}
