package com.example.myApp.dto.login;

import com.example.myApp.enity.Role;
import com.example.myApp.enity.User;

public class UserDTO {
    private String fullname;
    private String email;
    private String role;
    private int id;

    public UserDTO(String fullname, String email, String role, int id) {
        this.fullname = fullname;
        this.email = email;
        this.role = role;
        this.id = id;
    }

    public UserDTO(User user) {
        this.fullname = user.getFullname();
        this.email = user.getEmail();
        this.role = user.getRole() != null ? user.getRole().getName() : "USER"; // Đảm bảo không bị null
        this.id = user.getId();
    }
    public String getFullname() { return fullname; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public int getId() { return id; }

}
