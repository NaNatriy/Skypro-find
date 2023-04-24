package ru.skypro.homework.dto.userDTO;

import lombok.Data;

@Data
public class LoginReq {
    private String password;
    private String username;

}
