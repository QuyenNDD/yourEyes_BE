package com.example.myApp.controller;

import com.example.myApp.dto.login.UserDTO;
import com.example.myApp.enity.User;
import com.example.myApp.repository.UserRepository;
import com.example.myApp.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/getAll")
    public ResponseEntity<?> getAllUsers(HttpServletRequest request) {
        Integer roleId = (Integer) request.getAttribute("roleId");
        if (roleId != 2 || roleId == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bạn không có quyền");
        }
        List<UserDTO> userDTOS = userService.getAllUsers();
        return  ResponseEntity.ok(userDTOS);
    }
}
