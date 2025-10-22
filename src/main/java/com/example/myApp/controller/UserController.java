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
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/banUser/{userId}")
    public ResponseEntity<?> banUser(@PathVariable int userId, HttpServletRequest request){
        try{
            Integer roleId = (Integer) request.getAttribute("roleId");
            if (roleId == null || roleId != 2){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bạn không có quyền");
            }
            userService.banUser(userId);
            return ResponseEntity.ok("Ban user success!");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @PostMapping("/unbanUser/{userId}")
    public ResponseEntity<?> unbanUser(@PathVariable int userId, HttpServletRequest request){
        try{
            Integer roleId = (Integer) request.getAttribute("roleId");
            if (roleId == null || roleId != 2){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bạn không có quyền");
            }
            userService.unbanUser(userId);
            return ResponseEntity.ok("Unban user success!");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }
}
