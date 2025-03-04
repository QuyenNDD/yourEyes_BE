package com.example.myApp.service.serviceImpl;

import com.example.myApp.dto.login.RegisterRequest;
import com.example.myApp.enity.User;
import com.example.myApp.repository.UserRepository;
import com.example.myApp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder  bCryptPasswordEncoder;

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

        User user = User.builder()
                .fullname(request.getFullname())
                .email(request.getEmail())
                .password(bCryptPasswordEncoder.encode(request.getPassword())) // ✅ Mã hóa mật khẩu
                .phone(request.getPhone())
                .address(request.getAddress())
                .role(request.getRole()) // Nếu có role
                .build();

        userRepository.save(user);
    }

    @Override
    public boolean authenticate(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Sai tài khoản hoặc mật khẩu");
        }
        return true;
    }
}
