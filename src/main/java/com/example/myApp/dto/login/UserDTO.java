package com.example.myApp.dto.login;

import com.example.myApp.enity.Role;
import com.example.myApp.enity.User;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UserDTO {
    private final String fullname;
    private final String email;
    private final String phone;
    private final String address;
    private final String role;
    private final int id;

    public UserDTO(String fullname, String email, String phone, String address, String role, int id) {
        this.fullname = fullname;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.role = role;
        this.id = id;
    }

    public UserDTO(User user) {
        this.fullname = user.getFullname();
        this.email = user.getEmail();
        this.role = user.getRole() != null ? user.getRole().getName() : "USER"; // Đảm bảo không bị null
        this.id = user.getId();
        this.phone = user.getPhone();
        this.address = user.getAddress();
    }

}
