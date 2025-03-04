package com.example.myApp.dto.login;

import com.example.myApp.enity.Role;

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

    public String getFullname() { return fullname; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public int getId() { return id; }

}
