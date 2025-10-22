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

    public UserDTO(String fullname, String email, String phone, String address, Integer role, int id) {
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
        this.role = user.getRole() == 0 ? user.getRole() : 2;
        this.id = user.getId();
        this.phone = user.getPhone();
        this.address = user.getAddress();
    }

}
