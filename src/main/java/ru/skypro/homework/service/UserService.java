package ru.skypro.homework.service;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.userDTO.NewPassword;
import ru.skypro.homework.dto.userDTO.UserDTO;
import ru.skypro.homework.exception.NotFoundException;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.model.User;
import ru.skypro.homework.repository.UserRepository;

import java.io.IOException;

@Service
public class UserService {
    private final UserRepository UserRepository;
    private final PasswordEncoder encoder;

    public UserService(UserRepository UserRepository, PasswordEncoder encoder) {
        this.UserRepository = UserRepository;
        this.encoder = encoder;
    }

    public UserDTO findByUsername(Authentication authentication) {
        return UserMapper.userToUserDto(UserRepository.findByUsername(authentication.getName()).orElseThrow(NotFoundException::new));
    }

    public void updatePassword(NewPassword newPassword, Authentication authentication) {
        User user = UserRepository.findByUsername(authentication.getName()).orElseThrow(NotFoundException::new);
        if (!encoder.matches(newPassword.getCurrentPassword(), user.getPassword())) {
            throw new BadCredentialsException("Authentication exception");
        }
        user.setPassword(encoder.encode(newPassword.getNewPassword()));
        UserRepository.save(user);
    }

    public UserDTO update(UserDTO userDTO) {
        User user = UserRepository.findById(userDTO.getId()).orElseThrow(NotFoundException::new);
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setPhone(userDTO.getPhone());
        UserRepository.save(user);
        return userDTO;
    }

    public boolean updateAvatar(Authentication authentication, MultipartFile avatar) throws IOException {
        User user = UserRepository.findByUsername(authentication.getName()).orElseThrow(NotFoundException::new);
        user.setImage(avatar.getBytes());
        UserRepository.save(user);
        return true;
    }

    public byte[] getUserAvatar(Integer id) {
        return UserRepository.findById(id).orElseThrow(NotFoundException::new).getImage();
    }
}
