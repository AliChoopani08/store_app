package com.Ali.Store.App.user.validation;


import com.Ali.Store.App.dto.user.request.RegisterUserRequest;
import com.Ali.Store.App.testConfigs.TestSecurityConfig;
import com.Ali.Store.App.controller.user.AuthenticationController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationController.class)
@Import({ConfigValidationTest.class, TestSecurityConfig.class})
@AutoConfigureMockMvc()
public class ValidationAuthenticationClassTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;

    private RegisterUserRequest req;

    @BeforeEach
    void setUp() {
        req = RegisterUserRequest.builder()
                .username("09876543210")
                .password("fake.password")
                .build();
    }

    @Test
    void shouldShowErrorMassage_whenURLIsInvalid() throws Exception {
        mockMvc.perform(post("/invalid-url")
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(req)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Invalid URL"));
    }

    @Test
    void shouldShowErrorMassage_whenMethodAndURLAreNotMatch() throws Exception {
        mockMvc.perform(get("/auth/register")
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(req)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Being Match URL And Method"));
    }
}
