package com.example.myApp.dto.login;

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
    private final Integer role;
    private final int id;
    private final boolean isActive;

    public UserDTO(String fullname, String email, String phone, String address, Integer role, int id, boolean isActive) {
        this.fullname = fullname;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.role = role;
        this.id = id;
        this.isActive = isActive;
    }

    public UserDTO(User user) {
        this.fullname = user.getFullname();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.id = user.getId();
        this.phone = user.getPhone();
        this.address = user.getAddress();
        this.isActive = user.isActive();
    }
}
