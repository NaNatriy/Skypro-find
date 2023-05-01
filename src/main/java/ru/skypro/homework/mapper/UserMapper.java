package ru.skypro.homework.mapper;

import ru.skypro.homework.dto.userDTO.RegisterReq;
import ru.skypro.homework.dto.userDTO.UserDTO;
import ru.skypro.homework.model.User;

public class UserMapper {

    public static UserDTO userToUserDto(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getUsername());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setPhone(user.getPhone());
        userDTO.setImage("/users/me/image/" + user.getId());
        return userDTO;
    }

    public static User reqRegToUser(RegisterReq reg) {
        User user = new User();
        user.setUsername(reg.getUsername());
        user.setPassword(reg.getPassword());
        user.setFirstName(reg.getFirstName());
        user.setLastName(reg.getLastName());
        user.setPhone(reg.getPhone());
        return user;
    }


}