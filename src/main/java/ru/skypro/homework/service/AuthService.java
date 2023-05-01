package ru.skypro.homework.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.dto.userDTO.RegisterReq;
import ru.skypro.homework.dto.userDTO.UserDetailsDTO;
import ru.skypro.homework.exception.NotFoundException;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.model.User;


@Service
public class AuthService {

    private final UserDeService manager;

    private final PasswordEncoder encoder;

    public AuthService(UserDeService manager, PasswordEncoder encoder) {
        this.manager = manager;
        this.encoder = encoder;
    }

    public boolean login(String userName, String password) {
        if (!manager.userExists(userName)) {
            throw new NotFoundException();
        }
        UserDetails userDetails = manager.loadUserByUsername(userName);
        return encoder.matches(password, userDetails.getPassword());
    }

    public boolean register(RegisterReq registerReq) {
        registerReq.setUsername(registerReq.getUsername());
        registerReq.setPassword(registerReq.getPassword());
        registerReq.setPhone(registerReq.getPhone());
        User user = UserMapper.reqRegToUser(registerReq);
        user.setRole(Role.USER);
        UserDetailsDTO userDetailsDTO = new UserDetailsDTO(user);
        manager.createUser(userDetailsDTO);
        return true;
    }
}

