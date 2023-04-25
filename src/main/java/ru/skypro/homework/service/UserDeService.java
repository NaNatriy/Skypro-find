package ru.skypro.homework.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.userDTO.UserDetailsDTO;
import ru.skypro.homework.exception.NotFoundException;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.model.User;
import ru.skypro.homework.repository.UserRepository;

@Service
public class UserDeService implements UserDetailsManager {

    private final PasswordEncoder encoder;
    private final UserRepository UserRepository;

    public UserDeService(PasswordEncoder encoder, UserRepository UserRepository) {
        this.encoder = encoder;
        this.UserRepository = UserRepository;
    }

    @Override
    public void createUser(UserDetails user) {
        User saveUser = UserMapper.userDetailsDtoToUser((UserDetailsDTO) user);
        saveUser.setPassword(encoder.encode(user.getPassword()));
        UserRepository.save(saveUser);
    }

    @Override
    public void updateUser(UserDetails user) {

    }

    @Override
    public void deleteUser(String username) {

    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {

    }

    @Override
    public boolean userExists(String username) {
        return UserRepository.existsByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return UserMapper.userToUserDetailsDto(UserRepository.findByUsername(username).orElseThrow(NotFoundException::new));
    }
}
