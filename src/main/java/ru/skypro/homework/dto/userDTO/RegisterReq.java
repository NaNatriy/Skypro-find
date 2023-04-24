package ru.skypro.homework.dto.userDTO;

import lombok.Data;
import ru.skypro.homework.dto.Role;

@Data
public class RegisterReq {
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String phone;
    private Role role;
}
