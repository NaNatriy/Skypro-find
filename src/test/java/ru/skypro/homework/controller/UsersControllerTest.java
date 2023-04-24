package ru.skypro.homework.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
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
import ru.skypro.homework.model.User;
import ru.skypro.homework.repository.UserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@Testcontainers
@SpringBootTest
@AutoConfigureJson
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class UsersControllerTest {

    @Autowired
    private MockMvc mockMvcUsers;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private UserRepository userRepository;
    @BeforeEach
    void setUp() {
        User user = new User();
        user.setUsername("username");
        user.setFirstName("name");
        user.setLastName("name");
        user.setPhone("+78346767878");
        user.setPassword(encoder.encode("password"));
        user.setEnabled(true);
        user.setRole(Role.USER);
        userRepository.save(user);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "username", password = "password")
    void setPassword() throws Exception {

        mockMvcUsers.perform(post("/users/set_password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"currentPassword\": \"password\",\n" +
                                "  \"newPassword\": \"password2\"\n" +
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
}