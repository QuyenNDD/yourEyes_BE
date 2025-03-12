package com.example.myApp.service.serviceImpl;

import com.example.myApp.dto.UserUpdateRequest;
import com.example.myApp.dto.login.RegisterRequest;
import com.example.myApp.dto.login.UserDTO;
import com.example.myApp.enity.Role;
import com.example.myApp.enity.User;
import com.example.myApp.repository.RoleRepository;
import com.example.myApp.repository.UserRepository;
import com.example.myApp.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder  bCryptPasswordEncoder;
    private final RoleRepository roleRepository;

    @Override
    public Optional<User> loginEmail(String email, String password){
        return userRepository.findByEmail(email)
                .filter(user -> bCryptPasswordEncoder.matches(password, user.getPassword()));
    }

    @Override
    public void registerUser(RegisterRequest request){
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists!");
        }

        Role userRole = roleRepository.findByName("USER").
                orElseThrow(() -> new RuntimeException("Role không tồn tại"));

        User user = User.builder()
                .fullname(request.getFullname())
                .email(request.getEmail())
                .password(bCryptPasswordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .address(request.getAddress())
                .role(userRole) // Nếu có role
                .build();

        userRepository.save(user);
    }

    @Override
    public boolean authenticate(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Sai tài khoản hoặc mật khẩu"));

        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Sai tài khoản hoặc mật khẩu");
        }
        return true;
    }

    @Override
    public UserDTO getUserProfile(String email){
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        return new UserDTO(user);
    }

    @Override
    @Transactional
    public void updateUserProfile(String email, UserUpdateRequest userUpdateRequest){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setFullname(userUpdateRequest.getFullname());
        user.setPhone(userUpdateRequest.getPhone());
        user.setAddress(userUpdateRequest.getAddress());

        userRepository.save(user);
    }
}
