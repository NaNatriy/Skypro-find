package ru.skypro.homework.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockPart;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.dto.userDTO.UserDTO;
import ru.skypro.homework.exception.NotFoundException;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.model.User;
import ru.skypro.homework.repository.UserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@AutoConfigureMockMvc
@Testcontainers
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class UsersControllerTest {

    @Autowired
    private MockMvc mockMvcUsers;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository UserRepository;
    private final User user = new User();
    @BeforeEach
    void setUp() {
        user.setUsername("username");
        user.setFirstName("firstname");
        user.setLastName("lastname");
        user.setPhone("+79999999999");
        user.setPassword(encoder.encode("password"));
        user.setEnabled(true);
        user.setRole(Role.USER);
        user.setImage("userAvatar".getBytes());
        UserRepository.save(user);
    }

    @AfterEach
    void tearDown() {
        UserRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "username", password = "password")
    void setPassword() throws Exception {

        mockMvcUsers.perform(post("/users/set_password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"newPassword\": \"qwer1234\",\n" +
                                "  \"currentPassword\": \"password\"\n" +
                                "}"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "username", password = "password")
    void getUser() throws Exception {
        mockMvcUsers.perform(get("/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("username"));
    }

    @Test
    @WithMockUser(username = "username", password = "password")
    void updateUser() throws Exception {
        UserDTO userDTO = UserMapper.userToUserDto(UserRepository.findByUsername(user.getUsername()).orElseThrow(NotFoundException::new));
        userDTO.setEmail("username2");
        mockMvcUsers.perform(patch("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDTO.getId()))
                .andExpect(jsonPath("$.email").value("username2"));
    }

    @Test
    @WithMockUser(username = "username", password = "password")
    void updateUserImage() throws Exception {
        MockPart image = new MockPart("image", "avatar", "userAvatar".getBytes());
        mockMvcUsers.perform(patch("/users/me/image")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .with(request -> {
                    request.addPart(image);
                    return request;
                }))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "username", password = "password")
    void showAvatarOnId() throws Exception {
        User testUser = UserRepository.findByUsername(user.getUsername()).orElseThrow(NotFoundException::new);
        mockMvcUsers.perform(get("/users/me/image/" + testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(content().bytes("userAvatar".getBytes()));

    }
}