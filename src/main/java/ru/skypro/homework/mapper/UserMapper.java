package ru.skypro.homework.mapper;

import ru.skypro.homework.dto.userDTO.RegisterReq;
import ru.skypro.homework.dto.userDTO.UserDTO;
import ru.skypro.homework.dto.userDTO.UserDetailsDTO;
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

    public static UserDetailsDTO userToUserDetailsDto(User user) {
        UserDetailsDTO userDetailsDTO = new UserDetailsDTO();
        userDetailsDTO.setRole(user.getRole());
        userDetailsDTO.setUsername(user.getUsername());
        userDetailsDTO.setPassword(user.getPassword());
        userDetailsDTO.setFirstName(user.getFirstName());
        userDetailsDTO.setLastName(user.getLastName());
        userDetailsDTO.setPhone(user.getPhone());
        userDetailsDTO.setImage("/users/me/image/" + user.getId());
        userDetailsDTO.setEnabled(true);
        return userDetailsDTO;
    }

    public static User userDetailsDtoToUser(UserDetailsDTO userDetailsDTO) {
        User user = new User();
        user.setUsername(userDetailsDTO.getUsername());
        user.setFirstName(userDetailsDTO.getFirstName());
        user.setLastName(userDetailsDTO.getLastName());
        user.setPhone(userDetailsDTO.getPhone());
        user.setPassword(userDetailsDTO.getPassword());
        user.setEnabled(userDetailsDTO.isEnabled());
        user.setRole(userDetailsDTO.getRole());
        return user;
    }
}