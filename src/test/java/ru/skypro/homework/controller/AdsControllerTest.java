
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
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
class AdsControllerTest {

    @Autowired
    MockMvc mockMvcAds;
    @Autowired
    private AdsRepository adsRepository;
    @Autowired
    private UserRepository userRepository;
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
        user.setFirstName("name");
        user.setLastName("name");
        user.setPhone("+78346767878");
        user.setPassword(encoder.encode("password"));
        user.setEnabled(true);
        user.setRole(Role.USER);
        userRepository.save(user);

        createAds.setPrice(123);
        createAds.setTitle("title");
        createAds.setDescription("description");

        ads.setPrice(213);
        ads.setDescription("description");
        ads.setTitle("title");
        ads.setAuthor(user);
        ads.setImage("image".getBytes());

        adsRepository.save(ads);
    }

    @AfterEach
    void clear() {
        adsRepository.deleteAll();
        userRepository.deleteAll();
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
    void getAds() throws Exception {
        mockMvcAds.perform(get("/ads/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("title"))
                .andExpect(jsonPath("$.description").value("description"))
                .andExpect(jsonPath("$.price").value(213));
    }

    @Test
    @WithMockUser(username = "username", password = "password")
    void removeAd() throws Exception {
        mockMvcAds.perform(delete("/ads/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "username2", password = "password", roles = "ADMIN")
    void removeAdA() throws Exception {
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
    void updateAd() throws Exception {
        mockMvcAds.perform(patch("/ads/" + ads.getPk())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"price\": \"123\",\n" +
                                "  \"title\": \"title2\",\n" +
                                " \"description\": \"descriptionNew\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("title2"));

    }

    @Test
    @WithMockUser(username = "username2", password = "password", roles = "ADMIN")
    void updateAdA() throws Exception {
        mockMvcAds.perform(patch("/ads/" + ads.getPk())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"price\": \"15\",\n" +
                                "  \"title\": \"title2\",\n" +
                                " \"description\": \"descriptionNew\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("title2"));

    }

    @Test
    @WithMockUser(username = "username2", password = "password", roles = "ADMIN")
    void updateImageA() throws Exception {
        mockMvcAds.perform(patch("/ads/" + ads.getPk() + "/image")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(request -> {
                            request.addPart(image);
                            return request;
                        }))
                .andExpect(status().isOk());

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
    void showImage() throws Exception {
        mockMvcAds.perform(get("/ads/image/" + ads.getPk()))
                .andExpect(status().isOk())
                .andExpect(content().bytes("image".getBytes()));
    }
}
