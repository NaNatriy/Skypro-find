package ru.skypro.homework.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockPart;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.dto.adsDTO.CreateAdsDTO;
import ru.skypro.homework.model.Ads;
import ru.skypro.homework.model.User;
import ru.skypro.homework.repository.AdsRepository;
import ru.skypro.homework.repository.UserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
@Transactional
class AdsControllerTest {

    @Autowired
    MockMvc mockMvcAds;
    @Autowired
    private UserRepository UserRepository;
    @Autowired
    private AdsRepository adsRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PasswordEncoder encoder;
    private final MockPart image = new MockPart("image", "image", "image".getBytes());

    private final Ads ads = new Ads();
    private final CreateAdsDTO createAds = new CreateAdsDTO();
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

        createAds.setPrice(100);
        createAds.setTitle("title");
        createAds.setDescription("description");

        ads.setPrice(200);
        ads.setDescription("about");
        ads.setTitle("title");
        ads.setAuthor(user);
        ads.setImage("image".getBytes());

        adsRepository.save(ads);
    }

    @AfterEach
    void tearDown() {
        adsRepository.deleteAll();
        UserRepository.deleteAll();
    }

    @Test
    void getAllAds() throws Exception {
        mockMvcAds.perform(get("/ads"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.results").isArray())
                .andExpect(jsonPath("$.count").isNumber());
    }

    @Test
    @WithMockUser(username = "username", password = "password")
    void addAds() throws Exception {
        MockPart created = new MockPart("properties", objectMapper.writeValueAsBytes(createAds));

        mockMvcAds.perform(multipart("/ads")
                        .part(image)
                        .part(created))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pk").isNotEmpty())
                .andExpect(jsonPath("$.pk").isNumber())
                .andExpect(jsonPath("$.title").value(createAds.getTitle()))
                .andExpect(jsonPath("$.price").value(createAds.getPrice()));
    }

    @Test
    @WithMockUser(username = "username", password = "password")
    void getAds() throws Exception {
        mockMvcAds.perform(get("/ads/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("title"))
                .andExpect(jsonPath("$.description").value("about"))
                .andExpect(jsonPath("$.price").value(200));
    }

    @Test
    @WithMockUser(username = "username", password = "password")
    void removeAd() throws Exception {
        mockMvcAds.perform(delete("/ads/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "username2", password = "password")
    void removeAd_withOtherUser() throws Exception {
        mockMvcAds.perform(delete("/ads/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "username2", password = "password", roles = "ADMIN")
    void removeAd_withAdminRole() throws Exception {
        mockMvcAds.perform(delete("/ads/1"))
                .andExpect(status().isOk());
    }

    @Test
    void searchByTitle() throws Exception {
        mockMvcAds.perform(get("/ads/search?title=title"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.results").isArray())
                .andExpect(jsonPath("$.results[0].title").value("title"))
                .andExpect(jsonPath("$.count").isNumber());
    }

    @Test
    @WithMockUser(username = "username", password = "password")
    void updateAds() throws Exception {
        mockMvcAds.perform(patch("/ads/" + ads.getPk())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"price\": \"15\",\n" +
                        "  \"title\": \"title2\",\n" +
                        " \"description\": \"aboutNew\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("title2"));

    }

    @Test
    @WithMockUser(username = "username2", password = "password")
    void updateAds_withAnotherUser() throws Exception {
        mockMvcAds.perform(patch("/ads/" + ads.getPk())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"price\": \"15\",\n" +
                                "  \"title\": \"title2\",\n" +
                                " \"description\": \"aboutNew\"}"))
                .andExpect(status().isForbidden());

    }

    @Test
    @WithMockUser(username = "username2", password = "password", roles = "ADMIN")
    void updateAds_withRoleAdmin() throws Exception {
        mockMvcAds.perform(patch("/ads/" + ads.getPk())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"price\": \"15\",\n" +
                                "  \"title\": \"title2\",\n" +
                                " \"description\": \"aboutNew\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("title2"));

    }

    @Test
    @WithMockUser(username = "username", password = "password")
    void getAdsMe() throws Exception {
        mockMvcAds.perform(get("/ads/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.results").isArray())
                .andExpect(jsonPath("$.count").isNumber());
    }

    @Test
    @WithMockUser(username = "username", password = "password")
    void updateImage() throws Exception {
        mockMvcAds.perform(patch("/ads/" + ads.getPk() + "/image")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(request -> {
                            request.addPart(image);
                            return request;
                        }))
                .andExpect(status().isOk());

    }

    @Test
    @WithMockUser(username = "username2", password = "password")
    void updateImage_withOtherUser() throws Exception {
        mockMvcAds.perform(patch("/ads/" + ads.getPk() + "/image")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(request -> {
                            request.addPart(image);
                            return request;
                        }))
                .andExpect(status().isForbidden());

    }

    @Test
    @WithMockUser(username = "username2", password = "password", roles = "ADMIN")
    void updateImage_withRoleAdmin() throws Exception {
        mockMvcAds.perform(patch("/ads/" + ads.getPk() + "/image")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(request -> {
                            request.addPart(image);
                            return request;
                        }))
                .andExpect(status().isOk());

    }

    @Test
    void showImage() throws Exception {
        mockMvcAds.perform(get("/ads/image/" + ads.getPk()))
                .andExpect(status().isOk())
                .andExpect(content().bytes("image".getBytes()));
    }
}