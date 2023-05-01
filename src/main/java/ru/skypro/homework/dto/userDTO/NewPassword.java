package ru.skypro.homework.dto.userDTO;

import lombok.Data;

@Data
public class NewPassword {
    private String currentPassword;
    private String newPassword;
}
