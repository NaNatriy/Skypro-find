package ru.skypro.homework.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.dto.commentDTO.CommentDTO;
import ru.skypro.homework.model.Ads;
import ru.skypro.homework.model.Comment;
import ru.skypro.homework.model.User;
import ru.skypro.homework.repository.AdsRepository;
import ru.skypro.homework.repository.CommentRepository;
import ru.skypro.homework.repository.UserRepository;

import java.time.Instant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
class CommentControllerTest {
    @Autowired
    private MockMvc mockMvcComment;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AdsRepository adsRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CommentRepository commentRepository;
    private final User user = new User();
    private final Ads ads = new Ads();
    private final Comment comment = new Comment();

    @BeforeEach
    void setUp() {
        user.setUsername("username");
        user.setFirstName("name");
        user.setLastName("name");
        user.setPhone("+78346767878");
        user.setPassword(encoder.encode("password"));
        user.setEnabled(true);
        user.setRole(Role.USER);
        userRepository.save(user);

        ads.setPrice(200);
        ads.setDescription("description");
        ads.setTitle("title");
        ads.setAuthor(user);
        adsRepository.save(ads);

        comment.setText("text");
        comment.setAds(ads);
        comment.setAuthor(user);
        comment.setCreatedAt(Instant.now());
        commentRepository.save(comment);
    }

    @AfterEach
    void clear() {
        commentRepository.deleteAll();
        adsRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "username", password = "password")
    void getComments() throws Exception {
        mockMvcComment.perform(get("/ads/" + ads.getPk() + "/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.results").isArray())
                .andExpect(jsonPath("$.count").isNumber());
    }

    @Test
    @WithMockUser(username = "username", password = "password")
    void addComment() throws Exception {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setText("text2");
        mockMvcComment.perform(post("/ads/" + ads.getPk() + "/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("text2"));

    }

    @Test
    @WithMockUser(username = "username", password = "password")
    void deleteComment() throws Exception {
        mockMvcComment.perform(delete("/ads/" + ads.getPk() + "/comments/" + comment.getPk()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "username2", password = "password", roles = "ADMIN")
    void deleteCommentA() throws Exception {
        mockMvcComment.perform(delete("/ads/" + ads.getPk() + "/comments/" + comment.getPk()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "username", password = "password")
    void updateComment() throws Exception {
        mockMvcComment.perform(patch("/ads/" + ads.getPk() + "/comments/" + comment.getPk())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"text\": \"newText\"\n" +
                                "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("newText"));
    }

    @Test
    @WithMockUser(username = "username2", password = "password", roles = "ADMIN")
    void updateCommentA() throws Exception {
        mockMvcComment.perform(patch("/ads/" + ads.getPk() + "/comments/" + comment.getPk())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"text\": \"newText\"\n" +
                                "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("newText"));
    }
}