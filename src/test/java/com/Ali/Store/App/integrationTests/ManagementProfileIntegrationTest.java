package com.Ali.Store.App.integrationTests;

import com.Ali.Store.App.dto.user.request.ProfileRequest;
import com.Ali.Store.App.dto.user.request.RegisterUserRequest;
import com.Ali.Store.App.repository.userAndProfileUser.UserRepository;
import com.Ali.Store.App.testConfigs.TestConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static java.time.LocalDate.of;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestConfig.class)
public class ManagementProfileIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;

    private String jwt;

    @BeforeEach
    void setUp() throws Exception {
        userRepository.deleteAll();

        final RegisterUserRequest registerRequest = RegisterUserRequest.builder()
                .username("09112223344")
                .password("Password123")
                .build();

        final String registerResponseApi = mockMvc.perform(post("/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        jwt = objectMapper.readTree(registerResponseApi)
                .get("Access Token")
                .asText();
    }

    @Test
    void shouldUpdateProfileFields_whenUserHasBeenLoggedIn() throws Exception {
        final ProfileRequest updateProfileRequest = ProfileRequest.builder()
                .name("Ali Choopani")
                .email("abcd123@gmail.com")
                .birthData(of(2008, 3, 12))
                .build();

        mockMvc.perform(put("/profile")
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateProfileRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Your profile fields updated successfully"))
                .andExpect(jsonPath("$.data.username").value("09112223344"))
                .andExpect(jsonPath("$.data.Profile.name").value("Ali Choopani"));
    }

    @Test
    void shouldDisableTheActivatedAccount_whenUserHasBeenLoggedIn() throws Exception {
        mockMvc.perform(delete("/profile/account")
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldDisplayProfileFields_whenUserHasBeenLoggedIn() throws Exception {
        mockMvc.perform(get("/profile/me")
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("09112223344"));
    }
}
