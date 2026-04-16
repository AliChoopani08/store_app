package com.Ali.Store.App.integrationTests;

import com.Ali.Store.App.dto.user.request.ChangeUsernameRequest;
import com.Ali.Store.App.dto.user.request.PasswordResetRequest;
import com.Ali.Store.App.dto.user.request.PasswordVerifyRequest;
import com.Ali.Store.App.dto.user.request.UserRequest;
import com.Ali.Store.App.repository.userAndProfileUser.UserRepository;
import com.Ali.Store.App.testConfigs.TestJpaAuditingConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestJpaAuditingConfig.class)
class AuthenticationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository repositoryUser;

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String AUTHORIZATION = "Authorization";
    private String accessToken;
    private UserRequest userRequest;

    @BeforeEach
    void registering_some_users() throws Exception {
        repositoryUser.deleteAll();

        userRequest = UserRequest.builder()
                .username("09876543210")
                .password("Mohammad12ch")
                .deviceId("Android-Iphone-13-pro")
                .build();

        final String registerResponse = createUserAndAccessToken(userRequest);

        accessToken = objectMapper.readTree(registerResponse)
                .get("Access Token")
                .asText();
    }

    @Test
    void shouldChangeUsername_whenUserHadLoggedIn() throws Exception {
        final ChangeUsernameRequest req = new ChangeUsernameRequest("chopaniali373@gmail.com");

        mockMvc.perform(patch("/auth/me/username")
                        .with(csrf())
                        .header(AUTHORIZATION, BEARER_PREFIX + accessToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Username updated successfully"))
                .andExpect(jsonPath("$.data.username").value("chopaniali373@gmail.com"));
    }

    @Test
    void shouldResetPassword_whenPreviousPasswordAndPasswordVerifyTokenBeValid() throws Exception {
        /*
        Generate reset password token after being valid the previous password
         */
        final PasswordVerifyRequest pwdVerifyRequest = new PasswordVerifyRequest("Mohammad12ch");
        final String passwordVerifyResponse = mockMvc.perform(post("/auth/me/password-verify")
                        .header(AUTHORIZATION, BEARER_PREFIX + accessToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pwdVerifyRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("The password reset token created successfully"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        /*
        Get password reset token from returned json file
         */
        final String resetPasswordToken = objectMapper.readTree(passwordVerifyResponse)
                .get("data").get("Password Verify Token")
                .asText();

        /*
        Change password with generated password reset token
         */
        final PasswordResetRequest pwdResetRequest = new PasswordResetRequest(resetPasswordToken, "Ali.123@");
        mockMvc.perform(patch("/auth/me/password-reset")
                        .header(AUTHORIZATION, BEARER_PREFIX + accessToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pwdResetRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password reset successfully."));
    }

    @Test
    void shouldLogoutUser_whenUserLoggedIn() throws Exception {
        mockMvc.perform(delete("/auth/refresh-token")
                        .header(AUTHORIZATION, BEARER_PREFIX + accessToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldThrowException_whenUserHasAnActiveRefreshTokenAndWantsToCreateAnotherOne() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("This Refresh Token is already active in database. Please generate a new Access Token with this refresh token."));
    }


    private String createUserAndAccessToken(UserRequest registerReq) throws Exception {
        return mockMvc.perform(post("/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$['Access Token']").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }


}
