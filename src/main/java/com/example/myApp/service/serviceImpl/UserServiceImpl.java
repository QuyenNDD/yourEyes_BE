package com.example.myApp.service.serviceImpl;

import com.example.myApp.dto.ForgetPasswordRequest;
import com.example.myApp.dto.UserUpdateRequest;
import com.example.myApp.dto.login.RegisterRequest;
import com.example.myApp.dto.login.UserDTO;
import com.example.myApp.enity.User;
import com.example.myApp.repository.UserRepository;
import com.example.myApp.security.JwtTokenProvider;
import com.example.myApp.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder  bCryptPasswordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

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
                .password(bCryptPasswordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .address(request.getAddress())
                .role(1) // 2 là admin, 1 là user, 3 là employee
                .build();
        userRepository.save(user);
    }

    @Override
    public String authenticate(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Sai tài khoản hoặc mật khẩu"));
        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Sai tài khoản hoặc mật khẩu");
        }
        return jwtTokenProvider.generateToken(user);
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

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAllByOrderByIdAsc()
                .stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public String resetPassword(ForgetPasswordRequest request){
        if (request.getNewPassword() == null || request.getNewPassword().trim().isEmpty()) {
            throw new RuntimeException("Mật khẩu mới không được để trống!");
        }
        User user = userRepository.findByEmailAndPhone(request.getEmail(), request.getPhone())
                .orElseThrow(() -> new RuntimeException("Email hoặc số điện thoại không chính xác!"));

        user.setPassword(bCryptPasswordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);
        return "Mật khẩu đã được đặt lại thành công!";
    }

    @Override
    public void banUser(int id){
        userRepository.banUser(id);
    }

    @Override
    public void unbanUser(int id){
        userRepository.unbanUser(id);
    }
}
