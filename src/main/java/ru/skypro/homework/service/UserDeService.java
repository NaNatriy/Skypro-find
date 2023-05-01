package ru.skypro.homework.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.userDTO.UserDetailsDTO;
import ru.skypro.homework.exception.NotFoundException;
import ru.skypro.homework.model.User;
import ru.skypro.homework.repository.UserRepository;

import java.util.Optional;

@Service
public class UserDeService implements UserDetailsManager {

    //    private final PasswordEncoder encoder;
    private final UserRepository userRepository;

    public UserDeService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void createUser(UserDetails userDetails) {
        User user = new User(userDetails.getUsername(),
                userDetails.getPassword());
        userRepository.save(user);
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
        return userRepository.existsByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        User user = userOptional.orElseThrow(NotFoundException::new);
        return new UserDetailsDTO(user);
    }
}
