package ru.skypro.homework.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.dto.userDTO.LoginReq;
import ru.skypro.homework.dto.userDTO.RegisterReq;
import ru.skypro.homework.model.User;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.UserDeService;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@Testcontainers
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvcAuth;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserDeService userDetailsService;
    @Autowired
    private UserRepository UserRepository;
    private RegisterReq registerReq = new RegisterReq();
    private LoginReq loginReq;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setUsername("username");
        user.setFirstName("firstname");
        user.setLastName("lastname");
        user.setPhone("+79999999999");
        user.setPassword(encoder.encode("password"));
        user.setEnabled(true);
        user.setRole(Role.USER);
        UserRepository.save(user);


        registerReq.setUsername("username2");
        registerReq.setPassword("password");
        registerReq.setFirstName("testFirst");
        registerReq.setLastName("testLast");
        registerReq.setPhone("+79990001122");
        registerReq.setRole(Role.USER);

        loginReq = new LoginReq();
        loginReq.setPassword("password");
        loginReq.setUsername(user.getUsername());
    }

    @AfterEach
    void tearDown() {
        UserRepository.deleteAll();
    }

    @Test
    @WithAnonymousUser
    void login() throws Exception {
        mockMvcAuth.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void register() throws Exception {
        mockMvcAuth.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerReq)))
                .andDo(print())
                .andExpect(status().isOk());

        UserDetails userDetails = userDetailsService.loadUserByUsername(registerReq.getUsername());

        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails.getUsername(),
                userDetails.getPassword(),
                userDetails.getAuthorities());

        mockMvcAuth.perform(get("/users/me").with(authentication(auth))).andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(registerReq.getUsername()));
    }
}