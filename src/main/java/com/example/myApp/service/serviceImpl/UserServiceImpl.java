package com.example.myApp.service.serviceImpl;

import com.example.myApp.dto.request.ForgetPasswordRequest;
import com.example.myApp.dto.request.UserProfileRequest;
import com.example.myApp.dto.request.UserUpdateRequest;
import com.example.myApp.dto.login.RegisterRequest;
import com.example.myApp.dto.login.UserDTO;
import com.example.myApp.enity.User;
import com.example.myApp.enity.UserProfile;
import com.example.myApp.repository.UserProfileRepository;
import com.example.myApp.repository.UserRepository;
import com.example.myApp.security.JwtTokenProvider;
import com.example.myApp.service.UserService;

import com.example.myApp.service.python.ClusterPredictionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final UserProfileRepository userProfileRepository;
    private final ClusterPredictionService clusterService;

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
                .isActive(true)
                .build();
        userRepository.save(user);

        UserProfile userProfile = UserProfile.builder()
                .user(user)
                .gender(request.getGender())
                .age(request.getAge())
                .height(request.getHeight())
                .weight(request.getWeight())
                .stylePreference(request.getStylePreference())
                .build();
        userProfileRepository.save(userProfile);
        // --- (B) BƯỚC NÂNG CẤP 2: GỌI API PYTHON ĐỂ LẤY CỤM ---

        // (B.1) Tạo đối tượng Request DTO cho API Python
        UserProfileRequest apiRequest = new UserProfileRequest(
                request.getGender(),
                request.getAge(),
                request.getHeight(),
                request.getWeight(),
                request.getStylePreference()
        );

        // (B.2) Gọi API (đã được định nghĩa trong ClusterPredictionService)
        Optional<Integer> clusterIdOpt = clusterService.predictCluster(apiRequest);

        // (B.3) Xử lý kết quả và gán Cụm (Cluster)
        if (clusterIdOpt.isPresent()) {
            // Gán Cụm (Cluster) mà AI trả về
            userProfile.setClusterId(clusterIdOpt.get());
            System.out.println("AI: Gán user mới vào Cụm " + clusterIdOpt.get());
        } else {
            // Xử lý nếu API AI thất bại (ví dụ: gán cụm -1 (mặc định))
            System.err.println("Cảnh báo: Không thể gọi AI API. Gán Cụm mặc định (-1).");
            userProfile.setClusterId(-1); // Hoặc null, tùy thiết kế CSDL của bạn
        }

        // --- Phần 3: Lưu UserProfile (Giờ đã chứa clusterId) ---
        userProfileRepository.save(userProfile);
    }


@Override
public String authenticate(String email, String password) {
    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Sai tài khoản hoặc mật khẩu"));
    if (!user.isActive()){
        throw new BadCredentialsException("Tài khoản của bạn đã bị khóa");
    }
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
