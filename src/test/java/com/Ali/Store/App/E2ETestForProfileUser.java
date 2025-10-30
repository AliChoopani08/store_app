package com.Ali.Store.App;

import com.Ali.Store.App.dto.user.request.ProfileRequest;
import com.Ali.Store.App.dto.user.request.UserRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDate;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(PER_CLASS)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestJpaAuditingConfig.class)
public class E2ETestForProfileUser {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private String jwt;

    @BeforeAll
    void setUp() throws Exception {
        final UserRequest userRequest = new UserRequest("09112223344", "Password123", "sser315-55kg");

        final String registerResponseApi = mockMvc.perform(post("/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        jwt = objectMapper.readTree(registerResponseApi)
                .get("Access Token")
                .asText();
    }

    	@Test
	void update_profile() throws Exception {

            final ProfileRequest updateProfileRequest = new ProfileRequest("Ali Choopani", "09545685632", null, LocalDate.of(2008, 3, 12));

            mockMvc.perform(put("/profile")
				.header("Authorization", "Bearer " + jwt)
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateProfileRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.Profile.throw_exception_when_the_refresh_token_is_active_when_user_wants_to_login").value("Ali Choopani"));
	}

    @Test
    void delete_account() throws Exception {
        mockMvc.perform(delete("/profile/account")
                .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isNoContent());
    }

    @Test
    void display_my_profile() throws Exception {
        mockMvc.perform(get("/profile/me")
                .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("09112223344"));
    }
}
